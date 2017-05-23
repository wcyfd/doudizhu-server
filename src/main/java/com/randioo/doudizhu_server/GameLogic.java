package com.randioo.doudizhu_server;

import java.util.Arrays;

public class GameLogic {
	int[] pai = {
			0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D,//红桃
			//3   4     5     6     7     8     9     10    J     Q     K     A     2
			0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D,//黑桃

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
	public boolean checkPutOut(int[] pai){
		int[] outPai = toNum(pai);
		int len = outPai.length;
		if(len == 1){
			return true;
		}else if(len == 2){
			if(outPai[0] == outPai[1]){
				return true;
			}else if(outPai[0] >= 0xE && outPai[1] >= 0xE){
				return true;
			}
			else{
				return false;
			}
		}else if(len == 3){
			if(outPai[0] == outPai[1] && outPai[0] == outPai[2]){
				return true;
			}
			else return false;
		}else if(len == 4){
			Arrays.sort(outPai);
			if((outPai[0] == outPai[1] && outPai[0] == outPai[2]) || (outPai[3] == outPai[1] && outPai[3] == outPai[2])){
				return true;
			}
		}
		return false;
		
	}
	public int[] toNum(int[] pai){
		int[] temp = new int[pai.length];
		int i = 0;
		for(int t : pai){
			temp[i] = t & 0x0F;
			i++;
		}
		return temp;
	}
	
}
