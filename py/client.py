import socket
import threading

listen=1;
def listen_to_response(socket):
    while listen:
        data = socket.recv(1024).decode() # receive response
        print(data) # show in terminal

def client_program():
    host = "localhost" # Host name
    port = 1111 # socket server port number
    client_socket = socket.socket() # instantiate
    client_socket.connect((host, port)) # connect to the server
    thread=threading.Thread(target=listen_to_response, args=(client_socket,))
    thread.start()
    request=""
    while request.lower().strip() != 'bye':
        request = input(">")
        client_socket.send(request.encode()) # send message
    client_socket.close() # close the connection
if __name__ == '__main__':
    client_program()