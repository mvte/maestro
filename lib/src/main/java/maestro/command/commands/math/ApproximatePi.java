package maestro.command.commands.math;

import java.util.List;

import maestro.PrefixManager;
import maestro.command.CommandInterface;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ApproximatePi implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		PrivateChannel channel = event.getChannel().asPrivateChannel();
		String prefix = PrefixManager.PREFIXES.get(event.getGuild().getIdLong());
		
		if(args.isEmpty()) {
			channel.sendMessage("no argument provided\nusage: " + prefix + "approximatepi `<n>`, where n is the number of random numbers generated").queue();
			return;
		}
		
		int n;
		try {
			n = Integer.parseInt(args.get(0));
			
			if(n <= 0) {
				channel.sendMessage("please enter a number greater than 0").queue();
				return;
			}
			
		} catch (Exception e) {
			channel.sendMessage("was not able to parse an integer").queue();
			return;
		}
		
		//approximates pi by generating random points in a 1x1 square in the first quadrant
		//	this is using the assumption that the ratio of the number of points in a certain region vs the number of points in the entire region can reasonably estimate the ratio of the areas between the two regions
		//	thus, the number of points within an inscribed quarter circle / the number of points within a square ~= area of quarter circle / area of square
		//  	this gives ((pi(r)^2)/4) / (r^2) = pi/4 ->  multiply by 4 to get pi
		
		int ci = 0;
		double x, y;
		double approx, error;
		
		for(int i = 0; i < n; i++) {
			x = Math.random();
			y = Math.random();
			
			if((x*x + y*y) <= 1)
				ci++;
		}
		
		approx = ((double)ci/n)*4;
		error = 100*(Math.abs(approx-Math.PI)/Math.PI);
		
		
		channel.sendMessage(String.format("%f (*%.5f%% error*)", approx, error)).queue();
		
	}

	@Override
	public String getName() {
		return "approxpi";
	}

	@Override
	public String getHelp(String prefix) {
		return "horribly inefficient way of approximating the value of pi by using a random number generator\nusage: " + prefix + "approximatepi `<n>`, where n is the number of random numbers generated\nfor large numbers, it may take a while";
	}

}
