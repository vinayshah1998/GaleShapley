#!/usr/bin/env bash
inputDirectory="../ProvidedInputs"
expectedDirectory="../ExpectedOutputs"

SMALLFILES=("2-4-4" "4-8-5" "5-10-8" "8-8-8")
LARGEFILES=("50-100-100" "80-80-80" "100-200-180" "200-250-200")

smallCorrect=0
smallCount=0
largeCorrect=0
largeCount=0

rm *.class
javac Driver.java
#java Driver ${inputDirectory}/${file1}

if [[ -f differences.txt ]]; then
    rm differences.txt
fi

touch differences.txt

for i in ${SMALLFILES[@]};
do
    echo "Testing ${i}.in"
    outDirectory=${expectedDirectory}/small/output_${i}.txt
    inDirectory=${inputDirectory}/small/${i}.in

    smallCount=$((${smallCount} + 1))

    java Driver ${inDirectory} > smallFile
    git diff --no-index smallFile ${outDirectory} > test
    if [[ -s test ]]; then
        echo "Difference found for test case ${i}"
        cat test >> differences.txt

    else
        smallCorrect=$((${smallCorrect} + 1));
    fi
done


for i in ${LARGEFILES[@]};
do
    echo "Testing ${i}.in"
    outDirectory=${expectedDirectory}/large/output_${i}.txt
    inDirectory=${inputDirectory}/large/${i}.in
    largeCount=$((${largeCount} + 1))

    java Driver -g ${inDirectory} > largeFile

    git diff --no-index largeFile ${outDirectory} > test
    if [[ -s test ]]; then
        echo "Difference found for test case ${i}"
        cat test >> differences.txt
    else
        largeCorrect=$((${largeCorrect} + 1));
    fi
done

rm smallFile
rm largeFile

echo "----------------------------"
echo "   Set   |   Number Correct "
echo "----------------------------"
echo "  small  |        ${smallCorrect}/${smallCount}"
echo "  large  |        ${largeCorrect}/${largeCount}"
echo "----------------------------"
echo ""
echo "If there are any differences, you can check them in the differences.txt file"



