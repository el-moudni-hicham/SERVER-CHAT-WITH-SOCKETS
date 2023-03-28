import socket
import threading

listen = True

def listen_to_response(client_socket):
    while listen:
        response = client_socket.recv(1024).decode()
        print(response.strip())

def start_client():
    host = "localhost"
    port = 1111
    client_socket = socket.socket()
    client_socket.connect((host, port))
    thread = threading.Thread(target= listen_to_response, args= (client_socket,))
    thread.start()

    request = ""
    while request.lower().strip() != 'exit':
        request = input("")
        client_socket.send(request.encode())
    client_socket.close()
    listen = False

if __name__ == '__main__':
    start_client()
