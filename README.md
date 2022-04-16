# MessagingApp
## Java messaging App

Program:
JAVA 8,
Maven,
MYSQL,
docker,
Jersy server

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
A user(Sender) can send one or many messages this why there is a relation between the users and the messages as 1 to many where messages table has the sender id, a Sender and a receiver inherits from the users except the receiver has a relation with the table messages so that 1 message can have many receivers (1 message can be for many receivers) and 1 sender can send many messages (many messages can be for 1 sender), each message has a secret key which its decoded by. The secret key is inserted encoded as an array of bytes in the database.
The message body is inserted encoded in the database as an array of bytes.

### Program description:

A secured messaging application RESTful web service created by java 8  where the users the system can send encoded messages to each other and only the recipient can read the message by a key given to him. The messages are encoded with the public also the private key is generated for each message then inserted into the database encoded with the message id and the sender id. Also message body is encoded and inserted as Array of bytes inside the database, when each user wants to read the messages the messages are retrieved from database and decoded by the private key then they are given to the user.
also the program provides top 10 users (by sent message count) sorted by decreasing sent message count for the last 30 days

please feel free to see the app on the master branch.

Updates:
making second backup for the keys on excel file if the key not found in the database it will be searched from the excel file <br />
Remark: the code is on a regular updates.<br />
Coming update: encrypt the excel file<br />
Coming update: creating a react app which interact with the java backend<br />
Remark: to encrypt the file The RSA algorithm can only encrypt data that has a maximum byte length
of the RSA key length in bits divided with eight minus eleven padding
bytes, i.e. number of maximum bytes = key length in bits / 8 – 11.
Todo:  
generate a symmetric key
Encrypt the data with the symmetric key
Encrypt the symmetric key with rsa
send the encrypted key and the data
Decrypt the encrypted symmetric key with rsa
decrypt the data with the symmetric key
