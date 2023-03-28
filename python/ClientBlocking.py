import socket
import threading

class Client:
    def __init__(self):
        try:
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.socket.connect(('localhost', 1111))

            self.is_ = self.socket.makefile('r')
            self.os_ = self.socket.makefile('w')

            threading.Thread(target=self.receive_messages, daemon=True).start()

            while True:
                request = input()
                self.os_.write(request + '\n')
                self.os_.flush()

        except socket.error as e:
            raise RuntimeError(e)

    def receive_messages(self):
        try:
            while True:
                request = self.is_.readline().rstrip()
                if not request:
                    raise socket.error('Disconnected')
                print(request)

        except socket.error as e:
            raise RuntimeError(e)
if __name__ == '__main__':
    Client()