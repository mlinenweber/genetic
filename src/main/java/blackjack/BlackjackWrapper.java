package blackjack;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.IntWritable.Comparator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class BlackjackWrapper {

	public static Integer hands;
	public static Integer population;
	public static Double pct;
	public static Integer mutations;
	// hadoop jar Blackjack-0.0.1-SNAPSHOT-jar-with-dependencies.jar blackjack.BlackjackWrapper
	// <generations> <hands> <population> <pct> <mutations>
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		int generations = Integer.parseInt(args[0]);
		hands = Integer.parseInt(args[1]);
		population = Integer.parseInt(args[2]);
		pct = Double.parseDouble(args[3]);
		mutations = Integer.parseInt(args[4]);

		// create charts
		Configuration conf = new Configuration(true);
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path("blackjack/0/charts.txt");
		FSDataOutputStream out = fs.create(path);
    	for (int i=0;i<population;i++) {
    		Chart chart = new Chart();
    		out.writeBytes("0\t"+chart.toLine(";")+"\n");
    	}
    	out.close();

    	File file = new File("averages.txt");
		file.delete();
		
    	// map/reduce over charts
		for (int i=0; i<generations; i++) {
			System.out.println("Generation:"+i);
			doit(new Path("blackjack/"+i), new Path("blackjack/"+(i+1)));
			outputAverage(i+1);
		}
	}
	
	private static void outputAverage(int i) throws IOException {
		Configuration conf = new Configuration(true);
		FileSystem fs = FileSystem.get(conf);

		File file = new File("averages.txt");
    	FileOutputStream fos = new FileOutputStream(file, true);
    	BufferedOutputStream bos = new BufferedOutputStream(fos);
    	int c = 0;
    	int count = 0;
    	int tot = 0;
    	FSDataInputStream in = fs.open(new Path("blackjack/"+i+"/part-r-00000"));
    	String line = null;
    	while ((line = in.readLine()) != null) {
    		if (c >= population*pct) {
    			String[] split = line.split("\t");
    			Integer score = Integer.parseInt(split[0]);
    			tot += score;
    			count++;
    		}
    		c++;
    	}

    	bos.write((i + " "+(tot/count)+ "\n").getBytes());    	
		bos.close();
		fos.close();
	}
	
	private static int doit(Path inputPath, Path outputPath) throws IOException, InterruptedException, ClassNotFoundException {

		// Create configuration
		Configuration conf = new Configuration(true);
		conf.set("hands", hands.toString());
		conf.set("population", population.toString());
		conf.set("pct", pct.toString());
		conf.set("mutations", mutations.toString());

		// Create job
		Job job = new Job(conf, "Blackjack");
		job.setJarByClass(BlackjackMapper.class);

		job.setSortComparatorClass(Comparator.class);

		// Setup MapReduce
		job.setMapperClass(BlackjackMapper.class);
		job.setReducerClass(BlackjackReducer.class);
		job.setNumReduceTasks(1);

		// Specify key / value
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);

		// Input
		FileInputFormat.addInputPath(job, inputPath);
		job.setInputFormatClass(TextInputFormat.class);

		// Output
		FileOutputFormat.setOutputPath(job, outputPath);
		job.setOutputFormatClass(TextOutputFormat.class);

		// Delete output if exists
		FileSystem hdfs = FileSystem.get(conf);
		if (hdfs.exists(outputPath))
			hdfs.delete(outputPath, true);

		// Execute job
		int code = job.waitForCompletion(true) ? 0 : 1;
		return code;
	}

}
