import xlrd
import os
import glob
import numpy as np
from stacked_autoencoder import StackedAutoEncoder
from softmax import SoftMax
import tensorflow as tf
from flask import Flask
from flask import request
from crossdomain import crossdomain
application = Flask(__name__)

dirname = os.path.dirname(os.path.realpath(__file__)) + '/impeller wearing combination data'
sdamodel = None
softMaxModel = None

@application.route('/train',methods = ['GET','HEAD','OPTIONS'])
@crossdomain(origin='*'  , methods = ['GET','HEAD','OPTIONS'] , headers = ['Content-Type','Access-Control-Allow-Headers', 'Authorization', 'X-Requested-With'])
def train():
    data = []
    print(dirname)
    for filename in glob.glob(os.path.join(dirname, '*.xlsx')):
        print("filename")
        print(filename)
        workbook = xlrd.open_workbook(filename)
        for i in range(0,workbook.nsheets):
            sheet = workbook.sheet_by_index(i)
            data.append(sheet.col(0))
    print(data)   
    for i in range(0,len(data)):
        for j in range(0 , len(data[i])):
            data[i][j] = data[i][j].value
    
    print(data)   

    labels = [[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],
              [0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],[0,1],
              [1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],
              [1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0],[1,0]]

    data = np.array(data)
    labels = np.array(labels)
    # train / test  split
    idx = np.random.rand(data.shape[0]) <  1.0
    train_X, train_Y = data[idx], labels[idx]
    
    global sdamodel
    sdamodel = StackedAutoEncoder(dims=[50, 50], activations=['linear', 'linear'], epoch=[
                           2000, 2000], loss='rmse', lr=0.007, batch_size=len(train_X), print_step=200)
    sdamodel.fit(train_X)
    train_X_ = sdamodel.transform(train_X)
    global softMaxModel
    softMaxModel = SoftMax()
    softMaxModel.train_softmax(50,2,train_X_,train_Y,batch_size=len(train_X),epochs=500)
    return "success"
    
@application.route('/predict',methods = ['GET','HEAD','OPTIONS','POST'])
@crossdomain(origin='*'  , methods = ['GET','HEAD','OPTIONS','POST'] , headers = ['Content-Type','Access-Control-Allow-Headers', 'Authorization', 'X-Requested-With'])
def predict():
    request_data = request.get_json()
    print(request_data)
    request_data = np.array(request_data['values'])
    test_X = []
    test_X.append(request_data)
    test_X = np.array(test_X)
    print("-------------------")
    print(test_X)
    global sdamodel
    global softMaxModel
    test_X_ = sdamodel.transform(test_X)
    weights = np.array(softMaxModel.weights[0])
    biases = np.array(softMaxModel.biases[0])
    print(weights)
    print(biases)
    x = tf.placeholder(tf.float32, [None, 50])
    # Outputs and true y-values
    y_pred = tf.nn.softmax(tf.matmul(x, weights) + biases)

    sess = tf.Session()
    predictedValue = []
    for batch_xs in test_X_ :
        feed_dict = {x: [batch_xs]}
        sess.run(y_pred, feed_dict)
        prediction=tf.argmax(y_pred , 1)
        predictedValue.append(prediction.eval(feed_dict,session=sess))
        print(y_pred.eval(feed_dict,session=sess))
    sess.close()
    
    if(predictedValue[0][0] == 0):
        return "False"
    else:
        return "True"

if __name__ == "__main__":
   application.run()

    
