REM Copy the Node.tini file to each of the TINI boards on Robot 3.

REM ftp -v -n -i 192.168.3.1 < ftpcommands.txt
ftp -v -n -i 192.168.3.2 < ftpcommands.txt
ftp -v -n -i 192.168.3.3 < ftpcommands.txt
ftp -v -n -i 192.168.3.4 < ftpcommands.txt
ftp -v -n -i 192.168.3.5 < ftpcommands.txt
ftp -v -n -i 192.168.3.6 < ftpcommands.txt

