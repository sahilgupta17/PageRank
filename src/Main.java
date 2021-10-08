package src;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedReader;
import java.util.zip.GZIPInputStream; 
import java.lang.Math; 
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import java.io.*;
import java.util.Collections;

public class Main{
    private static Map<String, Set<String>> graph = new HashMap<>();
    private static Map<String, Double> initialPageRank = new HashMap<>();
    private static Map<String, Double> revisedPageRank = new HashMap<>();
    private static Map<String, Integer> inLinks = new HashMap<>();

    public static void load(String fileName){
        try{
            BufferedReader br = new BufferedReader(
                                new InputStreamReader(
                                new GZIPInputStream(
                                new FileInputStream(fileName)), "UTF-8"));
            String line = "";
            while( (line = br.readLine()) != null){
                String[] token = line.split("\t");
                String src = token[0], target = token[1];
                graph.putIfAbsent(src, new HashSet<>());
                graph.putIfAbsent(target, new HashSet<>());
                Set<String> outlinkSet = graph.get(src);
                outlinkSet.add(target); 
                graph.put(src, outlinkSet);
                inLinks.put(target, inLinks.getOrDefault(target, 0) + 1);
            }
            System.out.println(graph.size());
            br.close();
        }catch(Exception e){
            System.out.print(e.getMessage());
        }
    }
    public static void pageRank(double lambda, double tau){
        int graphSize = graph.size();
        graph.forEach((k, v)-> initialPageRank.put(k, 1.0 / graphSize));
        while(true){
            double toAdd = 0.0;
            graph.forEach((k, v)-> revisedPageRank.put(k, lambda / graphSize));
            for(String pageKey : graph.keySet()){
                int outlinkSetSize = graph.get(pageKey).size();
                if(outlinkSetSize > 0){
                    for(String outLink : graph.get(pageKey)){
                        revisedPageRank.put(outLink, computeScore(lambda, outlinkSetSize, pageKey, outLink));
                    }
                }else{
                    toAdd += (1-lambda)*initialPageRank.get(pageKey) / graphSize;
                }
            }
            for(String graphPage: graph.keySet()){
                revisedPageRank.put(graphPage, revisedPageRank.get(graphPage) + toAdd);
            }
            if(converge(tau))
                break;
            initialPageRank.forEach((k, v) -> initialPageRank.put(k, revisedPageRank.get(k)));
        }
    }
    public static double computeScore(double lambda, int outlinkSetSize, String page, String outLink){
        return revisedPageRank.get(outLink) + (1-lambda)*initialPageRank.get(page) / outlinkSetSize;
    }
    public static boolean converge(double tau){
        double norm2 = 0.0;
        for(Map.Entry<String, Double> page : initialPageRank.entrySet()){
            norm2 += Math.pow(Math.abs(initialPageRank.get(page.getKey()) - revisedPageRank.get(page.getKey())), 2);
        }
        double norm = Math.sqrt(norm2);
        return (norm < tau) ? true: false;
    }

    public static String sortinLinks(int n){
        List<Map.Entry<String, Integer>> list= new LinkedList<Map.Entry<String, Integer>>(inLinks.entrySet());

        // Sort the list using lambda expression
        Collections.sort(list,(i1,i2) -> i2.getValue().compareTo(i1.getValue()));
        StringBuffer content = new StringBuffer();
        int counter = 1;
        for (Map.Entry<String, Integer> page : list) {
            if(counter > n)
                break;
            content.append(counter + ". " + page.getKey() + " : " + page.getValue() + "\n");
            counter++;
        }
        return content.toString();
    }
    public static String sortPageRank(int n){
        List<Map.Entry<String, Double>> list= new LinkedList<Map.Entry<String, Double>>(revisedPageRank.entrySet());

        // Sort the list using lambda expression
        Collections.sort(list,(i1,i2) -> i2.getValue().compareTo(i1.getValue()));
        StringBuffer content = new StringBuffer();
        int counter = 1;
        for (Map.Entry<String, Double> page : list) {
            if(counter > n)
                break;
            content.append(counter + ". " + page.getKey() + " : " + page.getValue() + "\n");
            counter++;
        }
        return content.toString();
    }
    public static void writeFile(String filePath, String fileContent){
        try{
            Path inLinkFile = Path.of(filePath);
            Files.writeString(inLinkFile, fileContent);
        }catch(IOException e){
            System.out.print(e.getMessage());
        }
    }
    public static void main(String args[]){
        double lambda = Double.parseDouble(args[0]),  tau = Double.parseDouble(args[1]);
        String datafile = "./links.srt.gz";
        load(datafile);
        pageRank(lambda, tau);
        
        writeFile("./inlinks.txt", sortinLinks(75));
        writeFile("./pagerank.txt", sortPageRank(75));
    }
}