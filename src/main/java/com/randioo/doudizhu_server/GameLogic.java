package com.randioo.doudizhu_server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;



public class GameLogic {
	int[] pai = {
			0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D,//黑桃
			//3   4     5     6     7     8     9     10    J     Q     K     A     2
			0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D,//红桃

			0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D,//草花

			0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x4B, 0x4C, 0x4D,//方片

			0x0E, 0x0F
			//Joker
	};
	public static void main(String args[]){
		
	}
	public static int getType(int pai){
		return pai>>4;
	}
	public static int getNum(int pai){
		return (pai & 0x0F + 2) > 13 ? (pai & 0x0F + 2)-13 : (pai & 0x0F + 2) ;
	}
	public static int checkPutOut(int[] pai){
		int[] outPai = toNum(pai);
		int len = outPai.length;
		if(len == 1){
			return 1;//单张
		}else if(len == 2){
			if(outPai[0] == outPai[1]){
				return 2;//一对
			}else if(outPai[0] >= 0xE && outPai[1] >= 0xE){
				return 12;//双王
			}
			else{
				return -1;
			}
		}else if(len == 3){
			if(outPai[0] == outPai[1] && outPai[0] == outPai[2]){
				return 3;//三张
			}
			else {
				return -1;
			}
		}else if(len == 4){
			Arrays.sort(outPai);
			if((outPai[0] == outPai[1] && outPai[0] == outPai[2]) || (outPai[3] == outPai[1] && outPai[3] == outPai[2])){
				if(outPai[0] == outPai[3]){
					return 11;//炸弹
				}
				return 4;//三带一
			}
		}
		else if(len >= 5){
			Arrays.sort(outPai);
			if((len == 5) && ((outPai[0] == outPai[1] && outPai[0] == outPai[2] && outPai[3] == outPai[4]) || (outPai[4] == outPai[3] && outPai[4] == outPai[2] && outPai[0] == outPai[1]))){
				return 5;//三带二
			}
			else{
				boolean flag = true;
				if(outPai[len - 1] > 0xC){
					flag = false;
				}
				for(int i = 0 ; i < len - 2 ; i ++){
					if(outPai[i]+1 != outPai[i+1]){
						flag = false;
						break;
					}
				}
				if(flag){
					return 6;//单顺
				}
				if(len % 2 == 0){
					flag = true;
					if(outPai[len - 2] != outPai[len - 1] || outPai[len - 1] > 0xC){
						flag = false;
					}
					for(int i = 0 ; i < len - 3 ; i += 2){
						if(!(outPai[i] == outPai[i+1] && outPai[i] + 1  == outPai[i+2])){
							flag = false;
							break;
						}
					}
					if(flag){
						return 7;//姐妹
					}
				}
				if(len % 3 == 0){
					flag = true;
					if(outPai[len - 2] != outPai[len - 1] || outPai[len - 3] != outPai[len - 1] || outPai[len - 1] > 0xC){
						flag = false;
					}
					for(int i = 0 ; i < len - 4 ; i += 3){
						if(!(outPai[i] == outPai[i+1] && outPai[i] == outPai[i+2] && outPai[i] + 1  == outPai[i+3])){
							flag = false;
							break;
						}
					}
					if(flag){
						return 8;//三顺
					}
				}
				if(len == 6){
					//TODO XXXXAB
					HashMap <Integer,Integer> checkmap = new HashMap<>();
					for(int i = 0 ; i < outPai.length ; i ++){
						if(checkmap.get(outPai[i]) == null){
							checkmap.put(outPai[i], 1);
						}else{
							checkmap.put(outPai[i], checkmap.get(outPai[i])+1);
						}
					}
					if(checkmap.containsValue(4) && checkmap.keySet().size() == 3 && checkmap.values().size() == 2 &&(checkmap.containsValue(1)||checkmap.containsValue(2))){
						
					}
				}
				if(len == 8){
					//TODO XXXXAABB XXXYYYCD
				}
				if(len == 12 || len == 16 || len == 20){
					//TODO XXXYYYAB
				}
				if(len == 10 || len == 15 || len == 20){
					//TODO XXXYYYAABB
				}
				
				
			}
			return -1;
		}
		return -1;
		
	}
	public static int[] toNum(int[] pai){
		int[] temp = new int[pai.length];
		int i = 0;
		for(int t : pai){
			temp[i] = t & 0x0F;
			i++;
		}
		return temp;
	}
	public static HashMap <Integer,Integer> getPaiMap(int pai[]){
		int[] tempPai = toNum(pai);
		HashMap <Integer,Integer> checkmap = new HashMap<>();
		for(int i = 0 ; i < tempPai.length ; i ++){
			if(checkmap.get(tempPai[i]) == null){
				checkmap.put(tempPai[i], 1);
			}else{
				checkmap.put(tempPai[i], checkmap.get(tempPai[i])+1);
			}
		}
		return checkmap;
	}
	public static ArrayList<Integer>[] tearPai(int pai[]){
		HashMap <Integer,Integer> checkmap = getPaiMap(pai);
		ArrayList<Integer>[] tempPai = new ArrayList [4];
		for(ArrayList<Integer> t:tempPai){
			t.ensureCapacity(15);
		}
		for(int i = 0 ; i < 4 ; i ++){
			Iterator<Entry<Integer, Integer>> iter = checkmap.entrySet().iterator();
			while(iter.hasNext()){
				Entry<Integer, Integer> temp =  iter.next();
				if(temp.getValue() > i){
					tempPai[i].add(temp.getKey());
				}
			}
		}
		
		return tempPai;
	}
	
}
