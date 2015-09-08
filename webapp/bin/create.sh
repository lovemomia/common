#!/bin/bash

if [ $# -lt 2 ]; then
    echo "usage: $0 <outputDir> <projectName>" >&2
    exit 1
fi

cd `dirname $0`

OUTPUT_DIR=$1
PROJECT_NAME=$2
CAPITALIZED_PROJECT_NAME=`echo $PROJECT_NAME | awk '{printf "%s%s", toupper(substr($0,1,1)), substr($0,2)}'`

if [ -e $OUTPUT_DIR/service-$PROJECT_NAME ]; then
    echo "file or dir: $OUTPUT_DIR/service-$PROJECT_NAME exists"
    exit 1
fi

mkdir -p $OUTPUT_DIR/service-$PROJECT_NAME

cp -r bin $OUTPUT_DIR/service-$PROJECT_NAME/bin
cp -r conf $OUTPUT_DIR/service-$PROJECT_NAME/conf
cp -r src $OUTPUT_DIR/service-$PROJECT_NAME/src
cp -r web $OUTPUT_DIR/service-$PROJECT_NAME/web

cp assembly.xml $OUTPUT_DIR/service-$PROJECT_NAME/assembly.xml
cp LICENSE $OUTPUT_DIR/service-$PROJECT_NAME/LICENSE
cp pom.xml $OUTPUT_DIR/service-$PROJECT_NAME/pom.xml
cp README.md $OUTPUT_DIR/service-$PROJECT_NAME/README.md

sed -i '' "s/{PROJECT_NAME}/$PROJECT_NAME/g" $OUTPUT_DIR/service-$PROJECT_NAME/bin/app.sh
mv $OUTPUT_DIR/service-$PROJECT_NAME/web/WEB-INF/webapp-servlet.xml $OUTPUT_DIR/service-$PROJECT_NAME/web/WEB-INF/service-$PROJECT_NAME-servlet.xml
sed -i '' "s/{PROJECT_NAME}/$PROJECT_NAME/g" $OUTPUT_DIR/service-$PROJECT_NAME/web/WEB-INF/web.xml
sed -i '' "s/{CAPITALIZED_PROJECT_NAME}/$CAPITALIZED_PROJECT_NAME/g" $OUTPUT_DIR/service-$PROJECT_NAME/web/WEB-INF/web.xml
sed -i '' "s/{PROJECT_NAME}/$PROJECT_NAME/g" $OUTPUT_DIR/service-$PROJECT_NAME/pom.xml
sed -i '' "s/{CAPITALIZED_PROJECT_NAME}/$CAPITALIZED_PROJECT_NAME/g" $OUTPUT_DIR/service-$PROJECT_NAME/pom.xml
