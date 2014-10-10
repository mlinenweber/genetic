package blackjack;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class BlackjackReducer extends Reducer<IntWritable, Text, IntWritable, Text> {
	 
	private int c = 0;
	public void reduce(IntWritable score, Iterable<Text> charts, Context context)
			throws IOException, InterruptedException {
    	Integer population = Integer.parseInt(context.getConfiguration().get("population"));
    	Double pct = Double.parseDouble(context.getConfiguration().get("pct"));
    	Integer mutations = Integer.parseInt(context.getConfiguration().get("mutations"));

        for (Text chart : charts) {
          	context.write(c < population * pct ? new IntWritable(0) : score,
          		       	  c < population * pct ? new Text((new Chart()).toLine(";")) :
          		       		      new Text(Chart.fromLine(chart.toString()).mutate2(mutations).toLine(";")));
        	c++;
        }
	}
}