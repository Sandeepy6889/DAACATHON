# refactored from the examples at https://github.com/aymericdamien/TensorFlow-Examples
import tensorflow as tf
import mysql.connector as connector
import numpy as np


class TensorFlowRegressionModel:

    def __init__(self, config, is_training=True):
        # store the model variables into a class object
        self.vars = self.set_vars()
        self.model = self.build_model(self.vars)
        return

    def set_vars(self):
        """
        Define the linear regression model through the variables
        """
        return {
            # placeholders
            'X': tf.placeholder(tf.float64),
            'Y': tf.placeholder(tf.float64),
            # model weight and bias
            'W': tf.Variable(0.5, name="weight" , dtype = tf.float64),
            'b': tf.Variable(10.0, name="bias" , dtype = tf.float64)
        }

    def build_model(self, vars):
        """
        Define the linear regression model through the variables
        """
        return tf.add(tf.multiply(vars['X'], vars['W']), vars['b'])
    
    def connect_to_db(self):
        config = {
                'user': 'daacathon',
                'password': 'daacathon',
                'host': 'pumpmon.ca3q1lwdcehf.us-east-2.rds.amazonaws.com',
                'port': '3306',
                'database': 'pumpmonitor',
                }
        cnx = connector.connect(**config)
        return cnx


    def store_bias_and_weight(self, weight, bias, model_name,asset_id,connection):         
        cursor = connection.cursor()
        add_bias_and_weight = ("Insert into bias_and_weight"
                               "(asset_id,model_name,bias,weight)"
                               "values(%(asset_id)s,%(model_name)s,%(bias)s,%(weight)s)")
        data = {
          'asset_id'   : str(asset_id),
          'model_name' : str(model_name),
          'bias'       : float(bias),
          'weight'     : float(weight)
        } 
        cursor.execute(add_bias_and_weight,data)
        # Make sure data is committed to the database
        connection.commit()
        cursor.close()
        return
        
    def trainModel(self ,  learning_rate, training_epochs,asset_id ):
        connection = self.connect_to_db()
        cursor_for_truncate = connection.cursor()
        cursor_for_truncate.execute("DELETE from  bias_and_weight where asset_id = '" + asset_id+"'")
        cursor_for_truncate.close()
        cursor = connection.cursor()
        models = ["TDH","Efficiency"]
        i=0
        while i < len(models):
            train_X = []
            train_Y = []
            model_name =models[i] 
            queryString = "select Flow ,"+model_name+" from training_data where AssetId = '"+asset_id+"'" 
            cursor.execute(queryString)
            result_set  = cursor.fetchall()
            for row in result_set :
                train_X.append(row[0])
                train_Y.append(row[1])
                
            self.train(train_X, train_Y, learning_rate, training_epochs, model_name,asset_id,connection)
            i = i+1
        cursor.close()
        connection.close()
        return
            
            
    def train(self, train_X, train_Y, learning_rate, training_epochs, model_name,asset_id,connection):
        #n_samples = len(train_X)
        train_X = np.asarray(train_X)
        train_Y = np.asarray(train_Y)
        
        # Mean squared error
        cost = tf.reduce_mean(tf.pow(self.model - self.vars['Y'], 2))
        # Gradient descent
        optimizer = tf.train.AdamOptimizer(learning_rate).minimize(cost)
        wcoeff, bias = self.vars['W'], self.vars['b']
        # Launch the graph
        with tf.Session() as sess:
            sess.run(tf.global_variables_initializer())
            # Fit all training data
            for epoch in range(training_epochs): 
                for x, y in zip(train_X, train_Y):
                    sess.run(optimizer, feed_dict={self.vars['X']: x, self.vars['Y']: y})
                training_loss = sess.run(cost, feed_dict={self.vars['X']: train_X, self.vars['Y']: train_Y})
                print(training_loss)
                wcoeff, bias = sess.run([self.vars['W'], self.vars['b']])
            # Save model 
            self.store_bias_and_weight(wcoeff, bias, model_name,asset_id,connection) 
        return

    def predict(self, x_val,model,asset_id ):
        connection = self.connect_to_db()
        cursor = connection.cursor()
        get_weight_and_bias ="SELECT bias, weight FROM bias_and_weight WHERE model_name = '"+ model+"' AND asset_id ='" + asset_id+"'"
        cursor.execute(get_weight_and_bias)     
        for (bias, weight) in cursor:
            self.vars['W']=weight
            self.vars['b']=bias       
        cursor.close()
        connection.close()
        x_val = float(x_val)
        prediction = ((self.vars['W'] * x_val) + self.vars['b'])
        return str(prediction)