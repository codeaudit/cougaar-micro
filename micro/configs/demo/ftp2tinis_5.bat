REM Copy the Node.tini file to each of the TINI boards on Robot 5.

REM ftp -v -n -i 192.168.5.1 < ftpcommands.txt
ftp -v -n -i 192.168.5.2 < ftpcommands.txt
ftp -v -n -i 192.168.5.3 < ftpcommands.txt
ftp -v -n -i 192.168.5.4 < ftpcommands.txt
ftp -v -n -i 192.168.5.5 < ftpcommands.txt
ftp -v -n -i 192.168.5.6 < ftpcommands.txt

