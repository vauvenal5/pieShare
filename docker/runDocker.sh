cd ..
mvn clean install -DskipTests
cp -r ./pieShareAppFx/target/jfx/app ./docker
cd docker
sudo docker build .