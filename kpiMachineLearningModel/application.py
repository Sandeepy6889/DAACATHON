"""
Small script to run the regression model as a standalone code for training and testing purposes
"""
import configparser
import os

from tf_regression import TensorFlowRegressionModel
from flask import Flask
from flask import request
from crossdomain import crossdomain
application = Flask(__name__)

# get config file
HERE = os.path.dirname(os.path.realpath(__file__))
Config = configparser.ConfigParser()
Config.read(HERE + '/settings.ini')
# settings for the training
MODEL_DIR = Config.get('model', 'LOCAL_MODEL_FOLDER')
LEARNING_RATE = float(Config.get('model', 'LEARNING_RATE'))
TRAINING_EPOCHS = int(Config.get('model', 'TRAINING_EPOCHS'))

@application.route('/train',methods = ['GET','HEAD','OPTIONS'])
@crossdomain(origin='*'  , methods = ['GET','HEAD','OPTIONS'] , headers = ['Content-Type','Access-Control-Allow-Headers', 'Authorization', 'X-Requested-With'])
def train():
    # training data
    # Uncomment here for training again the model
    r = TensorFlowRegressionModel(Config)
    r.trainModel(LEARNING_RATE, TRAINING_EPOCHS,request.args.get('asset_id') )
    return 'success'

@application.route('/predict' , methods = ['GET','HEAD','OPTIONS'])
@crossdomain(origin='*' , methods = ['GET','HEAD','OPTIONS'] , headers = ['Content-Type','Access-Control-Allow-Headers', 'Authorization', 'X-Requested-With'])
def predict():    
    # make some predictions with the stored model
    #test_val = 10.5
    r = TensorFlowRegressionModel(Config, is_training=False)
    y_pred = r.predict(request.args.get('x'),request.args.get('model'),request.args.get('asset_id'))
    print(y_pred)
    return y_pred


if __name__ == "__main__":
   application.run()
