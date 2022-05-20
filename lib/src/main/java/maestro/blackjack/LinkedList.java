package maestro.blackjack;

import java.util.List;

import maestro.blackjack.objects.Player;

/**
 * I have to create my own linked list class so I can access the nodes :/
 * @author mute
 */
public class LinkedList {
	
	public static class Node {
		private Player player;
		public Node next;
		public Node prev;
		
		public Node(Player player, Node next) {
			this.player = player;
			this.next = next;
		}
		
		public Player getPlayer() {
			return this.player;
		}

		public boolean equals(Node cmp) {
			return cmp.getPlayer().equals(this.getPlayer());
		}
		
	}
	
	private Node head;
	
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
		head = new Node(players.get(0), null);
		
		prev = head;
		for(int i = 1; i < num; i++) {
			next = new Node(players.get(i), null);
			next.prev = prev;
			prev.next = next;
			prev = next;
		}
		
	}
	
	public Node getHeadNode() {
		return head;
	}
	

	
}
