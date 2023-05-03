#sprobi to novo skripto, fora je da na zacetku ne poslje prvic celga file ampak zacne samo nove vrstice(ker bova itak na fonu hranila zgodovino)
import bluetooth
import os
import time
from bluetooth import BluetoothSocket, RFCOMM

def send_new_data(sock, file_path, last_sent_line):
    with open(file_path, 'r') as file:
        lines = file.readlines()
        new_lines = lines[last_sent_line:]
        if new_lines:
            content = ''.join(new_lines) + "\n\n"
            sock.send(content.encode())
            return len(lines)
    return last_sent_line

server_sock = BluetoothSocket(RFCOMM)
server_sock.bind(("", bluetooth.PORT_ANY))
server_sock.listen(1)

uuid = "00001101-0000-1000-8000-00805F9B34FB"   # This UUID has to be the same as on Android Studio
bluetooth.advertise_service(server_sock, "Sample Bluetooth Server", service_id=uuid, service_classes=[uuid, bluetooth.SERIAL_PORT_CLASS], profiles=[bluetooth.SERIAL_PORT_PROFILE])

print("Waiting for connection on RFCOMM channel %d" % server_sock.getsockname()[1])

client_sock, client_info = server_sock.accept()
print("Accepted connection from", client_info)

try:

    # Replace 'example.txt' with your desired file path #
    file_path = r'./VBT_app_data.txt'
    last_mod_time = os.path.getmtime(file_path)

    # Get the initial number of lines in the file
    with open(file_path, 'r') as file:
        initial_lines = len(file.readlines())

    last_sent_line = initial_lines

    while True:
        current_mod_time = os.path.getmtime(file_path)
        if current_mod_time != last_mod_time:
            last_sent_line = send_new_data(client_sock, file_path, last_sent_line)
            print("New data sent.")
            last_mod_time = current_mod_time

        time.sleep(5)  # Adjust the sleep time to control how often the file is checked for changes

except KeyboardInterrupt:
    print("\nCtrl+C pressed. Exiting program.")
except OSError:
    pass

print("Disconnected.")

client_sock.close()
server_sock.close()



