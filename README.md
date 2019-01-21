# secure_file_transfer

This repo contains code for secure file transfer from server to a client. The server consists of a file of interest to the client. The server first receives a request from the client for the file. The server then encrypts the file. The server also generates a salt file and the IV(Initialization vector for CBC mode). The client takes all the 3 generated file and then generates a decrypted file.
