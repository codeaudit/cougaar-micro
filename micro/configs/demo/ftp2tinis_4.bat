REM Copy the Node.tini file to each of the TINI boards on Robot 4.

REM ftp -v -n -i 192.168.4.1 < ftpcommands.txt
ftp -v -n -i 192.168.4.2 < ftpcommands.txt
ftp -v -n -i 192.168.4.3 < ftpcommands.txt
ftp -v -n -i 192.168.4.4 < ftpcommands.txt
ftp -v -n -i 192.168.4.5 < ftpcommands.txt
ftp -v -n -i 192.168.4.6 < ftpcommands.txt

