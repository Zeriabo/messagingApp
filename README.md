# messagingApp
## Java messaging App

Database design:
![This is an image](/../master/assets/images/database.png)

## Database description: 

Above is the relational model of the database which consists of 5 tables :
- users
- sender
- receiver
- messages
- secret_keys
### Database Relations description:
A user(Sender) can send one or many messages this why there is a relation between the users and the messages as 1 to many where messages table has the sender id, a Sender and a receiver inherits from the users except the receiver has a relation with the table messages so that 1 receiver has many messages (1 message can be for many receivers) and 1 sender can send many messages (many messages can be for 1 sender), each message has a secret key which its decoded by the secret key is inserted encoded as an array of bytes in the database.
Also the message body is inserted encoded in the database as an array of bytes.

### Program description:

A secured messaging application RESTful web service created by java 8  where the users the system can send encoded messages to each other and only the recipient can read the message by a key given to him. The messages are encoded with the public also the private key is generated for each message then inserted into the database encoded with the message id and the sender id. Also message body is inserted as encoded inside the database, when each user wants to read the messages the messages are retrieved from database and decoded by the private key then they are given to the user.
