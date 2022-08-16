package com.ikhokha.techcheck;

import java.io.File;

import java.util.Map;
import java.util.concurrent.*;

public class Main {
	private static Map<String, Integer> totalResults = new ConcurrentHashMap<>();
	private static boolean debugEnabled = false;//Flag to enable debug log, thus by handling log size

	public static void main(String[] args) {

		try{
			File docPath = new File("docs");
			if(docPath.length()>0){//Run the logic is there are files.
				File[] commentFiles = docPath.listFiles((d, n) -> n.endsWith(".txt"));

				int totalFiles = commentFiles.length;
				System.out.println("Total Files -> "+totalFiles);

				//Get the actual available process allocated for the JVM, allocate half to core pool size
				int corePoolSize = (Runtime.getRuntime().availableProcessors()/2);
				int maxPoolSize = corePoolSize * 2;//

				System.out.println("Number of processors available to the Java Virtual Machine: "+Runtime.getRuntime().availableProcessors());
				System.out.println("corePoolSize -> "+corePoolSize);
				System.out.println("maxPoolSize -> "+maxPoolSize);

				//Thread Setup
				ThreadPoolExecutor threadPoolExecutor =
						new ThreadPoolExecutor(corePoolSize,
								maxPoolSize,
								15,
								TimeUnit.SECONDS,
								new LinkedBlockingQueue<>(),
								Executors.defaultThreadFactory());

				//Pooling execution
				for (File commentFile : commentFiles) {
					Runnable workerRunnable = new CommentsAnalyserRunnable(commentFile);
					threadPoolExecutor.execute(workerRunnable);
				}

				threadPoolExecutor.shutdown();

				while (!threadPoolExecutor.isTerminated()) {
					if(debugEnabled) {
						System.out.println("Active Thread Count -> " + threadPoolExecutor.getActiveCount() + " out of " + maxPoolSize);
					}
				}
				System.out.println("\n*****Finished all threads*****\n");

				System.out.println("RESULTS\n=======");
				totalResults.forEach((k,v) -> System.out.println(k + " : " + v));
			}else{
				System.out.println("NO FILES TO ANALYZE");
			}
		}catch (Exception e){
			System.err.println("Error accessing folder due to "+ e.getMessage());
		}



	}

	/**
	 * This method adds the result counts from a source map to the target map 
	 * @param source the source map
	 * @param target the target map
	 */
	private static void addReportResults(Map<String, Integer> source, Map<String, Integer> target) {

		for (Map.Entry<String, Integer> entry : source.entrySet()) {
			target.merge(entry.getKey(), entry.getValue(), Integer::sum);
		}

	}

	/**
	 * Runnable to handle the file reading and analysing
	 */
	public static class CommentsAnalyserRunnable implements Runnable {
		private final File commentFile;

		CommentsAnalyserRunnable(File commentFile) {
			this.commentFile = commentFile;
		}

		@Override
		public void run() {
			if(debugEnabled) {
				System.out.println(Thread.currentThread().getName() + " Start. commentFile = " + commentFile);
			}
			try {
				CommentAnalyzer commentAnalyzer = new CommentAnalyzer(commentFile);
				Map<String, Integer> fileResults = commentAnalyzer.analyze();
				addReportResults(fileResults, totalResults);
			} catch (Exception e) {
				System.out.println("Error Reading -> "+commentFile);
			}
			if(debugEnabled){
				System.out.println(Thread.currentThread().getName()+" End. commentFile = "+commentFile);
			}
		}
	}

}
