package maestro.command.commands.math;

import java.util.List;

import maestro.PrefixManager;
import maestro.command.CommandInterface;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CountPrimes implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		PrivateChannel channel = event.getChannel().asPrivateChannel();
		String prefix = PrefixManager.PREFIXES.get(event.getGuild().getIdLong());
		
		if(args.isEmpty()) {
			channel.sendMessage("no argument provided\nsee `" + prefix + "help countprimes`").queue();
			return;
		}
		
		if(args.size() > 2) {
			channel.sendMessage("too many arguments\nsee `" + prefix + "help countprimes`").queue();
			return;
		}
		
		int lo = 0, count = 0, n;
		
		for(String s : args) {
			try {
				int temp = Integer.parseInt(s);
				
				if(temp <= 0) {
					channel.sendMessage("all of ur arguments must be a number greater than 0").queue();
					return;
				}
				
			} catch (Exception e) {
				channel.sendMessage("was not able to parse an integer from one or more of your arguments").queue();
				return;
			}
		}
		
		if(args.size() == 2) {
			lo = Integer.parseInt(args.get(0));
			n = Integer.parseInt(args.get(1));
			
			// if the numbers are backwards, swap them
			if(lo > n) {
				int temp = lo;
				lo = n;
				n = temp;
			}
			
		} else {
			n = Integer.parseInt(args.get(0));
		}
		
		double startTime = System.nanoTime();
		
		for(int i = lo; i < n; i++) {
			if(checkPrime(i))
				count++;
		}
		
		double endTime = System.nanoTime();
		double totalTime = (endTime-startTime)/1000000000;
		
		String msg = String.format("%d primes `%.6fs`", count, totalTime);
		
		channel.sendMessage(msg).queue();
	}

	@Override
	public String getName() {
		return "countprimes";
	}

	@Override
	public String getHelp(String prefix) {
		return "counts the number of primes within a range of numbers\n\nusage:\n`" + prefix + "countprimes <n>` where n is the upper bound of counting or\n"
				+ "`" + prefix + "countprimes <n1> <n2>` where `n1` and `n2` define the range of counting\n"
				+ "\ncheck the source code to see how it works :D";
	}
	
	/**
	 * Checks if a number is prime by checking if that number has factors other than 1 and itself
	 * @param n the number to be checked
	 * @return true if the number is prime
	 */
	public static boolean checkPrime(int n) {
		/* How this works:
		 * For every factor of n, there is a corresponding factor (e.g. for n=100, 5 has a corresponding factor of 20)
		 * This means that it is redundant to check factors after the square root of n, since their corresponding factor 
		 * would be less than the square root of n. This algorithm can be made more efficient by knowing that all prime
		 * numbers are in the form 6k +- 1. This means, instead of checking if n is divisible by any number from 2 to the
		 * square root of n, it's more efficient to check if n is divisible by any numbers in the form 6k +- 1 to the square
		 * root of n. This is because all numbers are divisible by some prime number.
		 */
	
		if(n <= 3) {							//2 and 3 are prime
			return n>1;							//but 1 is not
		}
		if(n%2 == 0 || n%3 == 0) {				//checks if number is divisible by 2 or 3 first
			return false;						//because the proceeding algorithm does not
		}
		
		int i = 5; 								//5 is a prime in the form (6k-1) for k = 1;
		
		while(i*i <= n) {						//this ensures that we're only checking for values less than the square root of n	
			
			if(n%i == 0 || n%(i+2) == 0) {		//since i is in the form 6k-1, i+2 would be in the form 6k+1
				return false;					//returns false if divisible by either forms
			}
			i += 6;								//increments the k value by 1
		}
		return true;							//if only all tests have been found to be false
		
		//can find number of primes up to 10,000,000 in 1.27 seconds
		//can find number of primes up to 100,000,000 in 31.84 seconds
	}

}
