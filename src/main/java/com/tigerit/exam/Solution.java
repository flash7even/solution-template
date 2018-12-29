package com.tigerit.exam;
 
 
import static com.tigerit.exam.IO.*;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.StringTokenizer;
 
 
/**
 * All of your application logic should be placed inside this class.
 * Remember we will load your application from our custom container.
 * You may add private method inside this class but, make sure your
 * application's execution points start from inside run method.
 */
 
public class Solution implements Runnable {
     
    ArrayList<Table> List = new ArrayList<Table>();
    HashMap<String, Integer> Map = new HashMap<String, Integer>();
    ArrayList<Answer> Res = new ArrayList<Answer>();
    public static int nT, nQ;
     
    @Override
    public void run() {
        int T = readLineAsInteger();
        for(int cs = 1;cs<=T;cs++) {
            List.clear();
            Map.clear();
             
            nT = readLineAsInteger();
             
            // Get input of tables:
            for(int i = 0;i<nT;i++) {
                addTable(i);
            }
             
            System.out.println("Test: " + cs);
             
            // Handle queries:
            nQ = readLineAsInteger();
            for(int q = 0;q<nQ;q++) {
                Res.clear();
                processQuery();
            }
        }
         
    }
     
    // Split the String s into tokens based in delimiter del
    ArrayList<String> getTokens(String s, String del){
        StringTokenizer st = new StringTokenizer(s, del);
        ArrayList<String> list = new ArrayList<String>();
        while(st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        return list;
    }
     
    // Print the answer of a query
    void printAnswer() {
        Collections.sort(Res, new sortFunc());
        for(int i = 0;i<Res.size();i++) {
            for(int j = 0;j<Res.get(i).vals.size();j++) {
                if(j>0) System.out.print(" ");;
                System.out.print(Res.get(i).vals.get(j));
            }
            System.out.println();
        }
    }
     
    // After a query is given, find the answer for that query
    public void processQuery() {
        String line[] = new String[4];
        for(int l = 0;l<4;l++) {
            line[l] = readLine();
        }
        // Process 1st line:
        ArrayList<String> S1 = getTokens(line[0], "., ");
        ArrayList<String> S2 = getTokens(line[1], " ");
        ArrayList<String> S3 = getTokens(line[2], " ");
        ArrayList<String> S4 = getTokens(line[3], ".= ");
         
        boolean all = false;
        if(S1.size() == 2 && S1.get(1).equals("*")) all = true;
         
        String columnTitle1 = S4.get(2); 
        String columnTitle2 = S4.get(4);
         
        String tableName1 = S2.get(1);
        String tableName2 = S3.get(1);
 
        // Process 2nd line:
        if(S2.size() == 3) {
            String newName1 = S2.get(2);
            String newName2 = S3.get(2);
            for(int i = 1;i<S1.size();i+=2) {
                if(S1.get(i).equals(newName1)) {
                    S1.set(i, tableName1);
                }else if(S1.get(i).equals(newName2)) {
                    S1.set(i, tableName2);
                }
            }
            for(int i = 1;i<S4.size();i+=2) {
                if(S4.get(i).equals(newName1)) {
                    S4.set(i, tableName1);
                }else if(S4.get(i).equals(newName2)) {
                    S4.set(i, tableName2);
                }
            }
        }
         
        // Find the tables and columns
        Table T1 = List.get(Map.get(tableName1));
        Table T2 = List.get(Map.get(tableName2));
        int colIdx1 = T1.mark.get(columnTitle1);
        int colIdx2 = T2.mark.get(columnTitle2);
         
        // If Select *, add all attributes in S1
        if(all == true) {
            S1.remove(1);
            for(int i = 0;i<T1.titles.size();i++) {
                S1.add(T1.name);
                S1.add(T1.titles.get(i));
            }
            for(int i = 0;i<T2.titles.size();i++) {
                S1.add(T2.name);
                S1.add(T2.titles.get(i));
            }
        }
         
        // Actual join part
        for(int r1 = 0;r1<T1.records.size();r1++) {
            ArrayList<String> row1 = T1.records.get(r1);
            for(int r2 = 0;r2<T2.records.size();r2++) {
                ArrayList<String> row2 = T2.records.get(r2);
                if(row1.get(colIdx1).equals(row2.get(colIdx2))) {
                    Answer mrow = new Answer(); 
                    for(int i = 1;i<S1.size();i+=2) {
                        String table = S1.get(i);
                        String column = S1.get(i+1);
                        if(table.equals(T1.name)) {
                            int idx = T1.mark.get(column);
                            mrow.vals.add(row1.get(idx));
                        }else {
                            int idx = T2.mark.get(column);
                            mrow.vals.add(row2.get(idx));
                        }
                    }
                    Res.add(mrow);
                }
            }
        }
         
        // Printing part
        for(int i = 2;i<S1.size();i+=2) {
            if(i>2) System.out.print(" ");
            System.out.print(S1.get(i));
        }
        System.out.println();
        printAnswer();
        System.out.println();
        readLine();
    }
     
    // Read the whole input of a table
    public void addTable(int idx) {
        Table myt = new Table();
        myt.name = readLine();
         
        String line = readLine();
        ArrayList<String> S = getTokens(line, " ");
        myt.nC = Integer.valueOf(S.get(0));
        myt.nD = Integer.valueOf(S.get(1));
         
        String columns = readLine();
        myt.titles = getTokens(columns, " ");
        for(int i = 0;i<myt.titles.size();i++) {
            String name = myt.titles.get(i);
            myt.mark.put(name, i);
        }
         
        for(int r = 0;r<myt.nD;r++) {
            String row = readLine();
            myt.records.add(getTokens(row, " "));
        }
         
        List.add(myt);
        Map.put(myt.name, idx);
    }
}
 
class Table{ // Contains information of a table
    String name;
    int nD, nC;
    Table(){}
    ArrayList<String> titles = new ArrayList<String>();
    HashMap<String, Integer> mark = new HashMap<String, Integer>();
    ArrayList< ArrayList<String> > records = new ArrayList< ArrayList<String> >();
}
 
class Answer{ // Contains one row of the answer
    Answer(){}
    ArrayList<String> vals = new ArrayList<String>();
}
 
class sortFunc implements Comparator<Answer> { // Compare function to sort lexicographically
    public int compare(Answer a, Answer b) {
        for(int i = 0;i<a.vals.size();i++){
            int av = Integer.valueOf(a.vals.get(i));
            int bv = Integer.valueOf(b.vals.get(i));
            if(av == bv) continue;
            return av - bv;
        }
        return 0;
    }
}
