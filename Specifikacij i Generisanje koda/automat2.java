import java.util.LinkedList;
import java.util.function.Consumer;

public class AutomataDFA
{
	public boolean accept(String input, Reactions enteringState, Reactions exitingState, Reactions symbol0, Reactions symbol1)
	{
		String currState = new String("q0");
		for(var symbol : input.toCharArray())
		{
			switch (currState)
			{
				case "q1":
					exitingState.react(currState);
					if (symbol == '0')
					{
						symbol0.react(currState);
						currState = "q1";
					}
					if (symbol == '1')
					{
						symbol1.react(currState);
						currState = "q0";
					}
					enteringState.react(currState);
					break;
				case "q0":
					exitingState.react(currState);
					if (symbol == '0')
					{
						symbol0.react(currState);
						currState = "q0";
					}
					if (symbol == '1')
					{
						symbol1.react(currState);
						currState = "q1";
					}
					enteringState.react(currState);
					break;
			}
		}
		return currState.equals("q1");
	}
}
class Reactions
{
	public LinkedList<Consumer<String>> reactions = new LinkedList<>();
	public void react(String state)
	{
		reactions.forEach(value -> {
			value.accept(state);
		});
	}
}

