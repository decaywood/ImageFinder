package imageFinder.util;


/**
 * 2014年12月7日
 * @author decaywood
 *
 */
public class MinHeap {
    
    private HeapEntry[] heap;
    
    public static class HeapEntry{
        
        public String fileName;
        public double similarity;
        
    }
    
    public MinHeap(int TopN) {
        
        this.heap = new HeapEntry[TopN];
        for(int i = 0; i < heap.length; i++)
            heap[i] = new HeapEntry();
        
    }
    
    
    private void buildMinHeap(){
        for(int i = 0; i < heap.length >> 1; i++)
            minHeapify(i);
    }
    
    private void minHeapify(int i){
        
        int smallest = 0;
        
        boolean leftIsSmaller = left(i) < heap.length && smallerThanSecond(heap[left(i)], heap[i]);
        smallest = leftIsSmaller ? left(i) : i;
        
        boolean rightIsSmaller = right(i) < heap.length && smallerThanSecond(heap[right(i)], heap[smallest]);
        smallest = rightIsSmaller ? right(i) : smallest;
        
        if(smallest == i){ return; }
        
        exchangeEntry(heap[i], heap[smallest]);
        
        minHeapify(smallest);
         
    }
    
    private int left(int i){ return i << 1; }
    
    private int right(int i){ return (i << 1) + 1; }
    
    private boolean smallerThanSecond(HeapEntry entry1, HeapEntry entry2){
        
        double sim1 = entry1.similarity;
        double sim2 = entry2.similarity;
        return sim1 < sim2 ? true : false;
        
    }
    
    private void exchangeEntry(HeapEntry entry1, HeapEntry entry2){
        
        String tempFileName = entry1.fileName;
        double tempSim = entry1.similarity;
        
        entry1.fileName = entry2.fileName;
        entry1.similarity = entry2.similarity;
        entry2.fileName = tempFileName;
        entry2.similarity = tempSim;
        
    }
    
    public void addToHeap(String fileName, double similarity){
        
        if(similarity <= heap[0].similarity){ return; }
        
        HeapEntry entry = heap[0];
        entry.fileName = fileName;
        entry.similarity = similarity;
        
        buildMinHeap();
        
    }
    
    public HeapEntry[] returnResult(){ 
        
        HeapEntry[] result = new HeapEntry[heap.length];
        for(int i = result.length - 1; i >= 0; i--){
            result[i] = new HeapEntry();
            result[i].fileName = heap[0].fileName;
            result[i].similarity = heap[0].similarity;
            heap[0].similarity = Double.MAX_VALUE;
            buildMinHeap();
        }
        heap = null;
        return result;
    }
    

}
