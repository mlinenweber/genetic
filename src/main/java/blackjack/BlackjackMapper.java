package blackjack;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class BlackjackMapper extends Mapper<Object, Text, IntWritable, Text> {
	
    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {

    	String hands = context.getConfiguration().get("hands");
    	String[] split = value.toString().split("\t");
    	String chart = split[1];
    	int score = Blackjack.runGames(chart, Integer.parseInt(hands));
    	context.write(new IntWritable(score), new Text(chart));
    }
}