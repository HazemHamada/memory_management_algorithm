/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package memory_man;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Hazem
 */
public class Memory_man {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter number of frames");
        int f = sc.nextInt();
        System.out.println("Enter size of refrence string");
        int s = sc.nextInt();
        int[] a = new int[s];
        int[] MBits = new int[s];
        
        Random rand = new Random();
        for (int i = 0; i < s; i++) {
            a[i] = rand.nextInt(100);
            MBits[i] = rand.nextInt(2);
        }
        
        for (int i = 0; i < s; i++) {
            System.out.print(a[i] + " ");
        }
        System.out.println();
        for (int i = 0; i < s; i++) {
            System.out.print(MBits[i] + " ");
        }
        System.out.println();
        
        

//        int[] a={7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1};//2, 2, 2, 2, 5, 6, 5, 4, 3, 2
//        int[] MBits = {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1};//0, 0, 0, 0, 0, 1, 0, 1, 1, 0
//        int f = 3;
//        int s = a.length;
        int x = FIFO(f,a,s);
        System.out.println("FIFO = "+x);
        x = Second_Chance(f, a, s);
        System.out.println("SC = "+x);
        x = LFU(f,a,s);
        System.out.println("LFU = "+x);
        x = LRU(f,a,s);
        System.out.println("LRU = "+x);
        x  = EnhancedSecondChance(f, a, s, MBits);
        System.out.println("ESC = "+x);
        x = OPT(f,a,s);
        System.out.println("OPT = "+x);
        
    }
    
    static int FIFO(int frames, int[] ref, int refsize){
        int faults = 0;
        //Random r =new Random();
        //int frames =r.nextInt(19)+1;
        int[] q= new int[frames];
        for (int i=0;i<frames;i++)
            q[i]=1000;
        for(int i=0; i<refsize;i++){
            if(!isin(q,frames,ref[i])){
                    insertq(q,frames,ref[i]);
                    faults++;
            }
        }
        return faults;
    }
    
    static int Second_Chance(int frames, int[] ref, int refsize){
        int faults=0;
        int[] b=new int[frames];
        for(int i=0;i<frames;i++)
            b[i]=0;
        int[] q= new int[frames];
        for (int i=0;i<frames;i++)
            q[i]=1000;
        for(int i=0; i<refsize;i++){
            if(!isin(q,frames,ref[i])){
                if(q.length<frames){
                    insertq(q,frames,ref[i]);
                    faults++;
                }else{
                    int j;
                    for(j=frames-1;j>=0;j--){
                        if(b[j]==1){
                            b[j]=0;
                        }else if(b[j]==0){
                            insertqa(q,frames,j,ref[i]);
                            insertqa(b, frames,j, 0);
                            //q[j]=ref[i];
                            faults++;
                            break;
                        }
                    }
                }
            }else
                b[arrsearch(q, frames, ref[i])]=1;
        }
        
        return faults;
    }
    
    static int LFU(int frames, int[] ref, int refsize){
        int faults=0;
        int[] count=new int[frames];
        int[]q=new int[frames];
        for (int i=0;i<frames;i++)
            q[i]=1000;
        for(int j=0;j<refsize;j++){
            int min=1000, minref=0;
            int i=0;
            if(!isin(q,frames,ref[j])){
                for(i=0;i<frames;i++){
                    if(count[i]<min){
                        min=count[i];
                        minref=i;
                    }
                }
                q[minref]=ref[j];
                count[minref]=1;
                faults++;
            }else{
                  count[arrsearch(q, frames, ref[j])]++;
            }
        }
        return faults;
    }
    
    static int LRU(int frames, int[] ref, int refsize){
        int faults = 0;
        LinkedList<Integer> q = new LinkedList<>();
                
        for(int j = 0; j < refsize; j++){
            if(!q.contains(ref[j])) {
                if(j < frames) {
                    q.addFirst(ref[j]);
                    faults++;
                }
                else {
                    int LeastRecUsed = q.removeLast();// dy ll test
                    q.addFirst(ref[j]);
                    faults++;
                }
            }
            else { //btst5dm ka stack lw feh rakm felnos ytshal w yt7at fe el awl
               q.removeFirstOccurrence(ref[j]);
               q.addFirst(ref[j]);
            }
        }
        return faults;
    }
    
    static int EnhancedSecondChance(int frames, int[] ref, int refsize, int[] MBits){
        int faults = 0;
        int PTR = 0;
        int[][] q = new int[3][frames];
        Arrays.fill(q[0], -1);
        
        for(int j = 0; j < refsize; j++){
            if(!isin(q[0],frames,ref[j])) {
                if(j < frames) {
                q[0][PTR] = ref[j];
                q[1][PTR] = 0;
                q[2][PTR] = MBits[j]; 
                PTR = (PTR + 1) % frames;
                }
                else {
                    for (int z = 0; z < 2; z++) {
                        int counter = 0;
                        boolean FoundOnFirstLoop = false; 
                        while(counter != frames){
                            if(q[1][PTR] == 0 && q[2][PTR] == 0){// 1 refbit 2 modifybit
                                q[0][PTR] = ref[j];
                                q[1][PTR] = 0;
                                q[2][PTR] = MBits[j];
                                FoundOnFirstLoop = true;
                            }
                            else if(q[1][PTR] == 1) {
                                //q[1][PTR] = 0;//lw btclear fe awl mara
                            }
                            counter++;
                            PTR = (PTR + 1) % frames;
                            if(FoundOnFirstLoop)
                                break;
                        }
                        if(FoundOnFirstLoop)
                            break;

                        counter = 0;
                        boolean FoundOnSecondLoop = false;
                        while(counter != frames) {
                            if(q[1][PTR] == 0 && q[2][PTR] == 1){// 1 refbit 2 modifybit
                                q[0][PTR] = ref[j];
                                q[1][PTR] = 0;
                                q[2][PTR] = MBits[j];
                                FoundOnSecondLoop = true;
                            }
                            else{
                                q[1][PTR] = 0;//set refrence equal zero
                            }
                            counter++;
                            PTR = (PTR + 1) % frames;
                            if(FoundOnSecondLoop)
                                break;
                        }
                        if(FoundOnSecondLoop)
                            break;
                    }
                }
                faults++;
            }
            else { 
                int index = arrsearch(q[0], frames, ref[j]);
                q[1][index] = 1;//set refrence bit
                q[2][index] = MBits[j];
            }
        }
        return faults;
    }
    
    static int OPT(int frames, int[] ref, int refsize){
        int faults = 0;
        LinkedList<Integer> q = new LinkedList<>();
                
        for(int j = 0; j < refsize; j++){
            if(!q.contains(ref[j])) {
                if(j < frames) {
                    q.add(ref[j]);
                    faults++;
                }
                else {
                    int IndexToRemove = Predict(ref, q, refsize, j+1);
                    q.set(IndexToRemove, ref[j]);
                    faults++;
                }
            }
        }
        return faults;
    }
    
    static int Predict(int[] ref, List<Integer> fr, int refsize, int index){
        int res = -1, farthest = index; 
        for (int i = 0; i < fr.size(); i++) { 
            int j; 
            for (j = index; j < refsize; j++) { 
                if (fr.get(i) == ref[j]) { 
                    if (j > farthest) { 
                        farthest = j; 
                        res = i; 
                    } 
                    break; 
                } 
            } 

             
            if (j == refsize) //if page never refrenced in future return it
                return i; 
        } 
  
    //if all frames not refrenced we return 0 (first frame) 
    return (res == -1) ? 0 : res;     
    }
    
    
    
    static void insertqa(int[]arr, int size,int j,int x){
         for(int i=0;i<j;i++){
            arr[j-i]=arr[j-i-1];
        }
        arr[0]=x;
    }
    
    static int arrsearch(int[]arr, int size, int x){
        int index=0;
        for(int i=0;i<size;i++){
            if(arr[i]==x){
                index=i;
                break;
            }
        }
        return index;
    }
    
    static boolean isin(int[]arr, int size,int x){
        for(int i=0;i<size;i++){
            if(arr[i]==x)
                return true;
        }
        return false;
    }
    
    static void insertq(int[]arr, int size,int x){
        for(int i=1;i<size;i++){
            arr[size-i]=arr[size-i-1];
        }
        arr[0]=x;
    }
    
    
    
}
