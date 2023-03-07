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
>>>>>>> df45a44ebba5e577e58ccd0c4673f1866b7a95e4
