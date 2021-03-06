/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
/**
 * Huffman Tree Class
 * @author B. Programmer
 * @Date : 03/11/2019
 * * Used to create mappings for use in Huffman coding
 */


public class HuffmanTree implements Comparable<HuffmanTree>
{
	public int frequency = -1;
	public byte character;
	public boolean leafnode = false;
	public HuffmanTree left = null;
	public HuffmanTree right = null;
	
	/**
	  * Constructs a Huffman tree containing the specified item at the
	  * root, and a left and right Huffman tree as children.
	  * @param item item at the root of this Huffman tree
	  * @param b1 Huffman tree underneath to the left
	  * @param b2 Huffman tree underneath to the right
	  * @param f The characters frequency for sorting
	 **/
	public HuffmanTree(byte item, HuffmanTree b1, HuffmanTree b2, int f) {
		frequency = f;
		character = item;
		left = b1;
		right = b2;
		leafnode = true;
	}
	
	/**
	  * Constructs a Huffman tree containing no item at the
	  * root, and a left and right Huffman tree as children.
	  * @param b1 Huffman tree underneath to the left
	  * @param b2 Huffman tree underneath to the right
	  * @param f The characters frequency for sorting
	 **/
	public HuffmanTree(HuffmanTree b1, HuffmanTree b2, int f) {
		frequency = f;
		left = b1;
		right = b2;
	}
	
	
	/**
	  * Constructs an empty Huffman tree
	 **/
	public HuffmanTree() {
	}
	
	
	/**
	  * Determine if this Huffman tree is a leaf
	  * @return true if it is a leaf. False otherwise.
	 **/
	public boolean isLeaf() {
		return leafnode;
	}
	
	/**
	  * Get the character for the Huffman tree
	  * @return character for the Huffman tree
	 **/
	public byte getCharacter() {
		return character;
	}
	
	/**
	  * Get the left Huffman tree
	  * @return the left Huffman tree
	 **/
	public HuffmanTree getLeft() {
		return left;
	}
	
	/**
	  * Get the right Huffman tree
	  * @return the right Huffman tree
	 **/
	public HuffmanTree getRight() {
		return right;
	}
	
	/**
	  * Compares this Huffman tree with the specified Huffman tree for order.
	  * @return a negative integer, zero, or a positive integer as this object
	  * is less than, equal to, or greater than the specified object.
	 **/
	public int compareTo(HuffmanTree bt) {
			return frequency - bt.frequency;
	}
	
	
	/**
	  * Convert the Huffman tree into a boolean array for printing to file
	  * @return character for the Huffman tree
	 **/
	public boolean[] toBooleanArray(){
		//return new boolean[10];
		boolean[] boolArray = new boolean[1024];
		byte[] characters = new byte[1024];
		ArrayList<HuffmanTree> queue = new ArrayList();
		
		queue.add(this);
		boolArray[0] = false;
		int arraypos = 0;
		int position = 0;
		int characterpos = 0;
		while (position < queue.size()) {
			boolArray[arraypos] = queue.get(position).isLeaf();
			if (queue.get(position).isLeaf()) {
				characters[characterpos] = queue.get(position).getCharacter();
				characterpos++;
			}
			else {
				queue.add(queue.get(position).getLeft());
				queue.add(queue.get(position).getRight());
			}
			position++;
			arraypos++;
		}
		
		boolean[] ret = new boolean[arraypos + 8 * characterpos];
		for (int i = 0; i < arraypos; i++) {
			ret[i] = boolArray[i];
		}
		for (int i = 0; i < characterpos; i++) {
			byte characterByte = characters[i];
			boolean[] bits = BitByteConverter.byteToBooleanArray(characterByte);
			for (int j = 0; j < 8; j++) {
				ret[arraypos + (8 * i) + j] = bits[j];
			}
		}
		
		return ret;
	}
	
	
	/**
	  * Convert the Huffman tree to a boolean[][] for easily mapping bytes to
	  * their compressed bits
	  * @return A map of bytes to bits
	 **/
	public boolean[][] toArrayList() {
		boolean[][] ret = new boolean[256][];
		ArrayList<HuffmanTree> queue = new ArrayList();
		ArrayList<boolean[]> binary = new ArrayList();
		
		//System.out.println("ret.size() = " + ret.size());
		
		queue.add(this);
		binary.add(new boolean[0]);
		
		int position = 0;
		while (position < queue.size()) {
			//int toAdd = queue.get(position).getItem();
			boolean isLeaf = queue.get(position).isLeaf();
			if (!isLeaf) {
				boolean[] binaryString = binary.get(position);
				boolean[] binaryLeft = new boolean[binaryString.length + 1];
				boolean[] binaryRight = new boolean[binaryString.length + 1];
				for (int i = 0; i < binaryString.length; i++) {
					binaryLeft[i] = binaryString[i];
					binaryRight[i] = binaryString[i];
				}
				binaryLeft[binaryLeft.length - 1] = false;
				binaryRight[binaryRight.length - 1] = true;
				
				queue.add(queue.get(position).getLeft());
				binary.add(binaryLeft);
				queue.add(queue.get(position).getRight());
				binary.add(binaryRight);
			}
			else {
				int toAdd = (int) queue.get(position).getCharacter() + 128;
				ret[toAdd] = binary.get(position);
			}
			position++;
		}
		
		return ret;
	}
}