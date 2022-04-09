import java.io.*;
import java.util.Scanner;

public class Assignment10 {
    public static void main(String[] args) {

        PrintWriter finalGradePW = null;
        PrintWriter errorGradePW = null;

        try{

            //get user input for the file name STEP 1
            Scanner sc = new Scanner(System.in);
            System.out.print("Please enter the file name for the raw grades: ");
            String fileName = sc.nextLine();

            //STEP 3
            File finalGradesFile = new File("E:\\Java\\Labs Java\\TestIO\\src\\textFiles\\FinalGradesFile.txt");
            File errorGradesFile = new File("E:\\Java\\Labs Java\\TestIO\\src\\textFiles\\ErrorGradesFile.txt");

            //start iterating through the raw files and put them to the finalgradesfile and errorgradesfile STEP 4
            File rawGrades = new File("E:\\Java\\Labs Java\\TestIO\\src\\textFiles\\rawGrades.txt");
            Scanner input = new Scanner(rawGrades);

            finalGradePW = new PrintWriter(finalGradesFile);
            errorGradePW = new PrintWriter(errorGradesFile);
            while(input.hasNextLine()){
                String currLine = input.nextLine();
                String[] tokens = currLine.split(",");

                if(tokens[1].trim().isEmpty()){ //if there is no StudentID or if there is just spaces in the student ID STEP 4A
                    errorGradePW.println(currLine); //Step 4B
                }else{ //find the student's quiz average by finding all their quiz grades.

                    double finalQuizGrade = findQuizAvg(tokens);
                    double finalTestGrade = findTestAvg(tokens);

                    double finalGrade = (finalTestGrade * 0.75) + (finalQuizGrade * 0.25); //tests are worth 75%, quizzes 25%
                    char finalGradeLetter = getLetterGrade(finalGrade);

                    finalGradePW.println(tokens[0]+" "+tokens[1]+" "+finalGrade+" "+finalGradeLetter); //print the output to the text file.
                }


            }
            finalGradePW.close();//Very important, make sure you close access to the file to save changes.
            errorGradePW.close();

        } catch (FileNotFoundException e){ //We can't catch both FileNotFound and IOException as they catch each other.
            System.out.println("Error: System.IO.FileNotFoundException: Could not find file...");
        } catch (NumberFormatException e){ //STEP 4C
            System.out.println("Error: System.IO.FormatException: Input string was not in correct format...");
        }catch (IndexOutOfBoundsException e){ //STEP 4D
            System.out.println("Error: System.IndexOutOfRangeException: Index was outside the bounds of the array...");
        }finally {
            //if we opened a printwriter, close it no matter what STEP 5
            if(errorGradePW!=null){
                errorGradePW.close();
            }
            if(finalGradePW!=null){
                finalGradePW.close();
            }
        }
    }

    //This method will find the quiz average.
    public static double findQuizAvg(String[] tokens){
        double totalAmt = 0;
        double currLowestQuiz = Double.parseDouble(tokens[2]);
        for(int i=2;i<12;i++){ //calculate the quiz average starting at 1 since index 0 is the ID
            double currValue = Double.parseDouble(tokens[i]);
            if(currValue < currLowestQuiz){
                currLowestQuiz = currValue;
            }else{
                totalAmt += currValue;
            }
        }
        return (totalAmt - currLowestQuiz )/9; //Step 4E (Lowest test grade dropped) & STEP 4F (calculate Average)
    }

    //This method gets a letter grade from a numerical grade.
    public static char getLetterGrade(double grade){
        if(grade >= 89.5){
            return 'A';
        }else if(grade >= 79.5){
            return 'B';
        }else if(grade >= 69.5){
            return 'C';
        }else if(grade >= 59.5){
            return 'D';
        }else{
            return 'F';
        }
    }

    //This method calculates the total test average. This Should most likely be broken up,but for simplicity (and to not pass booleans back and forth, it's 1 method)
    public static double findTestAvg(String[] tokens){
        boolean cheated = false;
        double test1Grade = 0;
        double test2Grade = 0;
        double finalTestGrade = 0;

        boolean tookTest1 = false; //If they took either test.
        boolean tookTest2 = false;

        //Only 3 tests, so we do not have to use a loop. (but we can if we really wanted to)
        if(!tokens[14].trim().isEmpty()){ //i hardcode 14 here so that if we're missing a grade, we will go out of bounds and therefore trigger an exception.
            finalTestGrade = Double.parseDouble(tokens[14]);
            if(finalTestGrade == -1){ //if they cheated, they get a 0 and no replacement.
                cheated = true;
                finalTestGrade = 0;
            }
        }else{//They missed the test (or have no value for that test)
            finalTestGrade = 0;
        }

        if(!tokens[13].trim().isEmpty()){
            tookTest2 = true;
            test2Grade = Double.parseDouble(tokens[13]);
            if(test2Grade == -1){
                cheated = true;
                test2Grade = 0;
            }
        }else{ //They missed test 2
            if(!cheated){ //If they did not cheat, the final will replace the test 2 grade.
                test2Grade = finalTestGrade;
            }else{ //they cheated, so they get no replacement.
                test2Grade = 0;
            }
        }

        if(!tokens[12].trim().isEmpty()){
            tookTest1 = true;
            test1Grade = Double.parseDouble(tokens[12]);
            if(test1Grade == -1){
                cheated = true;
                test1Grade = 0;
            }
        }else{ //They missed test 2
            if(!cheated){ //If they did not cheat, the final will replace the test 2 grade.
                test1Grade = finalTestGrade;
            }else{ //they cheated, so they get no replacement.
                test1Grade = 0;
            }
        }

        //Check to see if the final will replace either test 1 or test 2 (Prereq: lower test 1 or test 2 grade AND didnt cheat.)
        if(!cheated && (tookTest1 && tookTest2)){ //if they didnt cheat
            if(test1Grade < test2Grade){ //if test 1 is the lowest test grade.
                if(test1Grade < finalTestGrade){ //if test 1 is lower than the final
                    test1Grade = finalTestGrade; //replace test 1 with the final
                }
            }else{ //test 2 is the lowest test grade
                if(test2Grade < finalTestGrade){ //if test 2 is lower than the final
                    test2Grade = finalTestGrade; //replace test 2 with the final
                }
            }
        }


        return  (test1Grade + test2Grade + finalTestGrade)/3;

    }
}
