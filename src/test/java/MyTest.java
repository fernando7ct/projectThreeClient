// MyTest.java
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.ArrayList;

class MyTest {

	@Test
	@DisplayName("Test Card constructor and getters")
	void testCard() {
		Card card = new Card('H', 12); // Queen of Hearts
		assertEquals('H', card.getSuit(), "Suit should be 'H'");
		assertEquals(12, card.getValue(), "Value should be 12");
	}

	@Test
	@DisplayName("Test PokerInfo constructor and getters/setters")
	void testPokerInfo() {
		PokerInfo info = new PokerInfo();
		info.setAction(PokerInfo.Action.PLACE_BET);
		info.setAnteBetRequest(10);
		info.setPairPlusBetRequest(5);
		info.setMessage("Placing bets");

		assertEquals(PokerInfo.Action.PLACE_BET, info.getAction(), "Action should be PLACE_BET");
		assertEquals(10, info.getAnteBetRequest(), "Ante bet should be 10");
		assertEquals(5, info.getPairPlusBetRequest(), "Pair Plus bet should be 5");
		assertEquals("Placing bets", info.getMessage(), "Message should be 'Placing bets'");
	}

	@Test
	@DisplayName("Test Player initial state")
	void testPlayerInitialState() {
		Player player = new Player();
		assertEquals(100, player.getTotalWinnings(), "Initial total winnings should be 100");
		assertEquals(0, player.getAnteBet(), "Initial Ante bet should be 0");
		assertEquals(0, player.getPlayBet(), "Initial Play bet should be 0");
		assertEquals(0, player.getPairPlusBet(), "Initial Pair Plus bet should be 0");
		assertTrue(player.getHand().isEmpty(), "Initial hand should be empty");
	}

	@Test
	@DisplayName("Test Player resetBets method")
	void testPlayerResetBets() {
		Player player = new Player();
		player.setAnteBet(10);
		player.setPlayBet(10);
		player.setPairPlusBet(10);
		player.setTotalWinnings(70);

		player.resetBets();

		assertEquals(0, player.getAnteBet(), "Ante bet should be reset to 0");
		assertEquals(0, player.getPlayBet(), "Play bet should be reset to 0");
		assertEquals(0, player.getPairPlusBet(), "Pair Plus bet should be reset to 0");
		assertEquals(100, player.getTotalWinnings(), "Total winnings should be reset to initial value");
	}

	/**
	 * Mock ClientMain to capture messages and round winnings.
	 */
	class ClientMainMock extends ClientMain {
		ArrayList<String> receivedMessages = new ArrayList<>();
		int roundWinnings = 0;

		@Override
		public void showResultsScreen(String msg, int roundWinnings) {
			receivedMessages.add(msg);
			this.roundWinnings = roundWinnings;
		}
	}

	/**
	 * Mock ClientNetworkThread to test sendInfo without actual networking.
	 */
	class ClientNetworkThreadMock extends ClientNetworkThread {
		ArrayList<PokerInfo> sentInfos = new ArrayList<>();

		public ClientNetworkThreadMock(String host, int port, ClientController controller) {
			super(host, port, controller);
		}

		@Override
		public void sendInfo(PokerInfo info) {
			sentInfos.add(info); // Simulate sending by adding to the list
		}
	}
}