ssh -i ~/.ssh/abarciauskas-bgse.pem ubuntu@ec2-54-82-243-48.compute-1.amazonaws.com

sudo apt-get update
sudo apt-get lftp
sudo apt-get postgresql-client-common
sudo apt-get postgres-xc-client
sudo apt-get install swig
sudo apt-get install make
sudo apt-get install g++

mkdir SPLIT_NORM
cd SPLIT_NORM
lftp sftp://text:informationretrieval@redrock.bsc.es -e "mget /home/vftp/text/GACETA/SPLIT_NORM/*2000*.txt; bye"

export DB_PASSWORD=$DB_PASSWORD

sudo apt-get install libboost-dev libboost-regex-dev libicu-dev libboost-system-dev libboost-program-options-dev libboost-thread-dev zlib1g-dev
sudo apt-get install libboost-regex-dev libicu-dev zlib1g-dev
sudo apt-get install libboost-system-dev libboost-program-options-dev libboost-thread-dev

wget https://github.com/TALP-UPC/FreeLing/releases/download/4.0/freeling-4.0-trusty-amd64.deb

sudo dpkg -i freeling-4.0-trusty-amd64.deb
