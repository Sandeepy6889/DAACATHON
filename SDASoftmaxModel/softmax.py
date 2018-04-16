import tensorflow as tf
import utils


class SoftMax:
    
    def __init__(self):
        self.weights, self.biases = [], []

    def train_softmax(self , input_dim, output_dim, x_train, y_train, lr=0.001, batch_size=100,
                      print_step=50, epochs=1):
        "Trains a softmax model for prediction."
        #Model input and parameters
        x = tf.placeholder(tf.float32, [None, input_dim])
        weights = tf.Variable(tf.truncated_normal(shape=[input_dim, output_dim], stddev=0.1))
        biases = tf.Variable(tf.constant(0.1, shape=[output_dim]))
        
        #Outputs and true y-values
        y_logits = tf.matmul(x, weights) + biases
        y_pred = tf.nn.softmax(y_logits)
        y_actual = tf.placeholder(tf.float32, [None, output_dim])
        
        #Cross entropy and training step
        cross_entropy = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits=y_logits, labels=y_actual))
        train_step = tf.train.AdamOptimizer(learning_rate=lr).minimize(cross_entropy)
        
        #Start session and run batches based on number of epochs
        sess = tf.Session()
        sess.run(tf.initialize_all_variables())
        
        step = 0
        #accuracy_history = []
        for i in range(epochs):
            b_x, b_y = utils.get_batch(
                    x_train, y_train, batch_size)
            print(b_x)
            sess.run(train_step, feed_dict={x: b_x, y_actual: b_y})
            #Debug
            if step == 100:
                break
            
            #Assess training accuracy for current batch
            if step % print_step == 0:
                correct_prediction = tf.equal(tf.argmax(y_pred, 1), tf.argmax(y_actual, 1))
                accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
                accuracy_val = sess.run(accuracy, feed_dict={x: b_x, y_actual: b_y})
                print("Step %s, current batch training accuracy: %s" % (step, accuracy_val))
                #accuracy_history = append_with_limit(accuracy_history, accuracy_val)
                
                #Assess training accuracy for last 10 batches
                if step > 0 and step % (print_step * 10) == 0:
                    print("Predicted y-values:\n", sess.run(y_pred, feed_dict={x: b_x}))
                    #print("Overall batch training accuracy for steps %s to %s: %s" % (step - 10 * print_step,step,average(accuracy_history)))                    
                    step += 1
        
        self.weights.append(sess.run(weights))  
        self.biases.append(sess.run(biases))                      
        sess.close()
        return