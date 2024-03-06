# Requirements
- Maven
- Docker
- Docker-compose (installed separately from Docker)

# Running / redeploying
## Linux / macOS
`make`

## Windows
Look at the file named `Makefile` and run the same commands in the root directory of the project

# Accessing
## REST server
http://localhost:8080

You should get a congratulation message. If not, try again a few seconds later - the database initialization takes some time. If it still doesn't work, look at the logs.

## MariaDB database
localhost:3306

Username is `root` and password is `root_password`

# Developing
The SQL statements in `database.sql` are run on every redeploy on the empty database, so evolving the database schema works just like code. Change the statements, redeploy and there it is. No need for `ALTER TABLE` statements.

Note that it is not possible to run the REST server outside of Docker.

# Debugging
## REST server
<<<<<<< HEAD
Java remote debug port is at `localhost:5005`
=======
Java remote debug port is at `localhost::8080`
>>>>>>> df45a44ebba5e577e58ccd0c4673f1866b7a95e4

Logs can be read with `docker logs `

## MariaDB database
<<<<<<< HEAD
Logs can be read with `docker logs messaging-db`
=======
Logs can be read with `docker logs messaging-db`
#descripton


Let's break down the encryption and decryption process in your code.

Encryption:
Generate RSA Key Pair:

In MessageServiceImpl.sendMessages, a new RSA key pair is generated using RSAKeyPairGenerator.
The public key is used to encrypt the message body before sending it.
The private key is stored in the secret_keys table for later decryption.
Encrypting Message:

The message body is encrypted using the recipient's public key.
The encrypted message is stored in the messages table.
Storing Private Key:

The private key is stored in the secret_keys table along with the associated message and sender IDs.
Decryption:
Retrieve Private Key:

In MessageServiceImpl.getMessages, the private key associated with a message is retrieved from the secret_keys table.
Decrypt Message:

The encrypted message is decrypted using the private key obtained in the previous step.
The decrypted message is then added to the list of messages.
Backup:
Backup and Encryption:

The decrypted messages are stored in an Excel file (GFGsheetDecrypted.xlsx).
This file is then encrypted using a symmetric key (secretKeyDecrypted).
The symmetric key is obtained during the encryption process.
File Deletion:

The original decrypted Excel file is deleted after encryption to maintain security.
Symmetric Key Handling:

The symmetric key is saved in a file (semetrickey.key) and used for decryption during subsequent read operations.

