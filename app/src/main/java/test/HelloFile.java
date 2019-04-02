package test;

import java.io.*;

/**
 * Copyright © 2013-2018 Worktile. All Rights Reserved.
 * Author: SongJian
 * Email: songjian@worktile.com
 * Date: 2019/3/31
 * Time: 15:47
 * Desc:
 */
public class HelloFile {
    public static void main(String[] args) throws IOException {
        File readiedFile = new File("C:\\Users\\22257\\Desktop\\Androiddemo\\bugoutDemo-master\\gradle.properties");
        File myFile = new File("hello.txt");

        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        if (isExit(readiedFile)) {
            try {
                bufferedReader = new BufferedReader(new FileReader(readiedFile));
                bufferedWriter = new BufferedWriter(new FileWriter(myFile));
                String oneline = null;
                while ((oneline = bufferedReader.readLine()) != null) {
                    System.out.println(oneline);
                    if (!isExit(myFile)) myFile.createNewFile();
                    bufferedWriter.write(oneline + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            }
        }
    }

    public static boolean isExit(File myFile) {
        if (myFile.exists()) {
            System.out.println(myFile.isFile());
            System.out.println(myFile.isDirectory());
            return true;
        } else {
            System.out.println("do not exit");
            return false;
        }
    }
}
