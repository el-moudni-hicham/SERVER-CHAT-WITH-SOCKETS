import socket

def main():
    host = 'localhost'
    port = 1111
    while True:
        clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        clientSocket.connect((host, port))
        message = input("type your message ... \n")
        clientSocket.send(message.encode())

if __name__ == '__main__':
    main()