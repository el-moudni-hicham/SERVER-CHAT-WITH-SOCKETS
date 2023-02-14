import socket
import threading


def main():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect(('localhost', 1111))
        print("Connected to server")

        with s.makefile('r') as is_, s.makefile('w') as os:
            def recv_message():
                while True:
                    request = is_.readline().strip()
                    if not request:
                        print("Disconnected from server")
                        break
                    print(request)

            recv_thread = threading.Thread(target=recv_message)
            recv_thread.start()

            while True:
                message = input("> ")
                if not message:
                    break
                os.write(message.encode() + b'\n')
                os.flush()

if __name__ == '__main__':
    main()
