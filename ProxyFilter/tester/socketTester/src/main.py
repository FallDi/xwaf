'''
Created on 03.05.2012

@author: daredevil
'''

import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(('127.0.0.1', 8888))
request = 'GET http://zaic101.ru HTTP/1.0\r\n\r\n'
sendBytes = s.send(bytes(request, "utf-8"))
print(sendBytes)
print(s.recv(1024))
s.close();