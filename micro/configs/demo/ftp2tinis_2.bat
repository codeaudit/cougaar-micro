REM Copy the Node.tini file to each of the TINI boards on Robot 2.

REM ftp -v -n -i 192.168.2.1 < ftpcommands.txt
ftp -v -n -i 192.168.2.2 < ftpcommands.txt
ftp -v -n -i 192.168.2.3 < ftpcommands.txt
ftp -v -n -i 192.168.2.4 < ftpcommands.txt
ftp -v -n -i 192.168.2.5 < ftpcommands.txt
ftp -v -n -i 192.168.2.6 < ftpcommands.txt

