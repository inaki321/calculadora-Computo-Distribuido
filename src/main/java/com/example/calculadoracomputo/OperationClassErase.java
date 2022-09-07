package com.example.calculadoracomputo;

import java.util.ArrayList;
import java.util.List;

public class OperationClassErase {
    public static void main(String args[]){
        String test = "1 + 2 * 3";

        //character to get in each iteration
        Character numberaux ;

        //aux number to use in iteration before an space
        String numberFromString ="";

        //suma a ir sumando,restando etc
        List<String> operationsArray = new ArrayList<String>();


        for (int i = 0; i <= test.length()-1; i = i + 1) {
            numberaux = test.charAt(i);
            if(numberaux ==' '){ //remove blank spaces and get complete numbers before new number
                operationsArray.add(numberFromString);
                numberFromString = "";
            }
            else{
                numberFromString = numberFromString + numberaux;
            }
            if(test.length() -1 == i){
                operationsArray.add(numberFromString);
            }
        }



        float val=0, res=0, lastVal=0;

        String opreationSymbol ="";
        for (int i = 0; i <= operationsArray.size()-1; i = i + 1) {

            try {
                val = Float.parseFloat(operationsArray.get(i));
                if(opreationSymbol.equals("+")){
                    lastVal = lastVal + val;
                }
                else if(opreationSymbol.equals("-")){
                    lastVal = lastVal - val;
                }
                else if(opreationSymbol.equals("/")){
                    lastVal = lastVal / val;
                }
                else if(opreationSymbol.equals("*")){
                    lastVal = lastVal * val;
                }
                //for the first iteration just get the last value
                if(i == 0){
                    lastVal = val;
                }
            } catch (Exception e) {
                opreationSymbol = operationsArray.get(i);
                Thread.currentThread().interrupt();
            }
            System.out.println("----------------------------  ");
        }
        System.out.println("resultado: "+lastVal);
    }
}
