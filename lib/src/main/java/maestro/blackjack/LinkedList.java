package maestro.blackjack;

import java.util.List;

import maestro.blackjack.objects.Player;

/**
 * I have to create my own linked list class so I can access the nodes :/
 * @author mute
 */
public class LinkedList {
	
	/**
	 * A public Node class, so that Game can access its fields
	 * @author mvte
	 */
	public static class Node {
		private Player player;
		public Node next;
		public Node prev;
		
		/**
		 * Constructs a node given the Player, next Node, and prev Node
		 * @param player
		 * @param next
		 * @param prev
		 */
		public Node(Player player, Node next, Node prev) {
			this.player = player;
			this.next = next;
			this.prev = prev;
		}
		
		/**
		 * Gets the player for this node
		 * @return the Player object of this node
		 */
		public Player getPlayer() {
			return this.player;
		}
		
	}
	
	private Node head;
	
	/**
	 * Constructs an empty linked list
	 */
	public LinkedList() {
		head = null;
	}
	
	/**
	 * Adds all the players of a collection to the linked list
	 * @param players
	 */
	public LinkedList(List<Player> players) {
		if(players.isEmpty()) {
			head = null;
			return;
		}
		
		Node prev, next;
		int num = players.size();
		head = new Node(players.get(0), null, null);
		
		prev = head;
		for(int i = 1; i < num; i++) {
			next = new Node(players.get(i), null, null);
			next.prev = prev;
			prev.next = next;
			prev = next;
		}
		
	}
	
	/**
	 * Gets the node at the front of the list
	 * @return the node at the front of the list
	 */
	public Node getHeadNode() {
		return head;
	}
	

	
}
