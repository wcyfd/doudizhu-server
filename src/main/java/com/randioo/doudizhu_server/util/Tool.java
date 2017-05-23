package com.randioo.doudizhu_server.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;



public class Tool {
	
	
	public static Method getFunction(@SuppressWarnings("rawtypes") Class $class , String funcname)
	{
	    Method method = null;
	    Method[] methodList =   $class.getDeclaredMethods();
		for ( int i = 0 ; i < methodList.length ; i ++)
		{
			Method m = methodList[i];
			if (m.getName().equals(funcname))
			{
				method = m;
			}
		}
	    return method;
	}
	
	/**打乱数组顺序*/
	public static int[] randomList(int[] $list)
	{
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<Integer> list=new ArrayList();  
		for(int t=0; t  < $list.length; t++){    
			list.add($list[t]);    
		}  
		
		int [] copytmp = new int[list.size()];
		Random random = new Random();
		int len = list.size();
		
		for (int i = 0 ; i < len; i ++)
		{
			int _index = random.nextInt(list.size());
			copytmp[i] = (int) list.get(_index);
			list.remove(_index);
		}
		return copytmp;
	}
	public static int [] arrayCopy(int [] src , int[] dest)
	{
		if (dest == null)return src;
		int [] list = new int[src.length + dest.length];
		System.arraycopy( src, 0, list, 0, src.length);
		System.arraycopy( dest, 0, list, src.length, dest.length);
		return list;
	}
	
	public static int [] arrayCopy(int [] src , int[] dest, int loc)
	{
		if (dest == null)return src;
		int [] list = new int[src.length + dest.length];
		
		System.arraycopy( dest, 0, list, 0, 13*loc);
		System.arraycopy( src, 0, list, 13*loc, src.length);
		System.arraycopy( dest, 13*loc, list, 13*loc+src.length, dest.length-13*loc);
		return list;
	}
	
	/**打乱数组顺序*/
	public static String[] randomList(String[] $list)
	{
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<String> list=new ArrayList();  
		for(String t : $list){    
			list.add(t);    
		}  
		String [] copytmp = new String[list.size()];
		Random random = new Random();
		int len = list.size();
		
		for (int i = 0 ; i < len; i ++)
		{
			int _index = random.nextInt(list.size());
			copytmp[_index] = (String) list.get(_index);
			list.remove(_index);
		}
		return copytmp;
	}
	
	public static int indexOf(int [] $list , int $target)
	{
		int len = $list.length;
		
		for (int i = 0 ; i < len; i ++)
		{
			if ($list[i] == $target)
			{
				return i;
			}
		}
		return -1;
	}
	/**依据某个索引删除数组一个元素*/
	public static int[] removeItemForList(int[] $list, int $index)
	{
		
		int[] tmp = new int[$list.length - 1];
		int len = $list.length;
		int index = 0;
		for (int i = 0 ; i < len; i++)
		{
			if (i!=$index)
			{
				tmp[index] = $list[i];
				index++;
			}
		}
		return tmp;
		
	}
	public static int[] removeItemForList2(int[] $list, int $index)
	{
		if($index == -1)return $list;
		return removeItemForList($list,$index);
	}
	/**依据某个索引删除数组一个元素*/
	public static int[] removeItem(int[] $list, int $deleteTarget)
	{
		int[] tmp = new int[$list.length - 1];
		int len = $list.length;
		int index = 0;
		boolean isDelete = false;
		for (int i = 0 ; i < len; i++)
		{
			if (isDelete== false && $list[i] == $deleteTarget)
			{
				isDelete = true;
				continue;
			}
			tmp[index] = $list[i];
			index++;
		}
		return tmp;
	}
	/**取含四个相同的数字*/
	public static int getSame4(int [] $list , int $target)
	{
		int [] tList = $list.clone();
		tList = addItemToList(tList, $target);
		Arrays.sort(tList);
		int len = tList.length -3;
		for(int i = 0; i < len ;i++ )
		{
			if (tList[i] == tList[i+1] && tList[i+2] == tList[i+1]&& tList[i+2] == tList[i+3])
			{
				return tList[i];
			}
		}
		
		return -1;
	}
	/**依据某个索引删除数组一个元素*/
	public static String[] removeItemForList(String[] $list, String comp)
	{
		String[] tmp = new String[$list.length - 1];
		int len = $list.length;
		int index = 0;
		for (int i = 0 ; i < len; i++)
		{
			if ($list[i].indexOf(comp) > -1)
			{
				continue;
			}
			tmp[index] = $list[i];
			index++;
		}
		return tmp;
	}
	
	public static boolean theItemIsInList(int [] $list, int checkTarget)
	{
		int len = $list.length;
		for (int i = 0 ; i < len; i++)
		{
			if ($list[i] == checkTarget)
			{
				return true;
			}
		}
		return false;
	}
	
	public static int [] addItemToList(int [] $list,int addTarget)
	{
		int[] tmp = new int[$list.length + 1];
		int len = $list.length;
		for (int i = 0 ; i < len; i++)
		{
			tmp[i] = $list[i];
		}
		tmp[$list.length] = addTarget;
		return tmp;
	}
	public static ArrayList<Integer> chang(int[] $list)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		int len = $list.length;
		for (int i = 0 ; i < len; i ++)
		{
			list.add($list[i]);
		}
		
		list.remove(0);
		return list;
	}
	/***/
	public static int [] remove123(int [] _list)
	{
		Arrays.sort(_list);
		if (_list.length< 3)
		{
			return _list;
		}
		for (int i = 0; i< _list.length ; )
		{
			int num0 = _list[i];
			if (num0 > 400)
			{
				i++;
				continue;
			}
			int num1 = Tool.indexOf(_list, num0+1);
			int num2 = Tool.indexOf(_list, num0+2);
			if (num1 != -1 && num2 != -1)
			{
				_list = Tool.removeItem(_list, num0);
				_list = Tool.removeItem(_list, num0+1);
				_list = Tool.removeItem(_list, num0+2);
				i = 0;
				continue;
			}
			i++;
		}
		return _list;
	}

	/**移除所以三个相同的。 */
	public static int [] removeBase3(int[] _list)
	{
		Arrays.sort(_list);
		if (_list.length< 3)
		{
			return _list;
		}
		int len = _list.length;
		int maxIndex = _list.length -2;
		for (int i = 0; i< maxIndex ;)
		{
			int num0 = _list[i];
			if ( _list[i+1] == _list[i+2] && _list[i+2] == num0)
			{
				_list = Tool.removeItem(_list, num0);
				_list = Tool.removeItem(_list, num0);
				_list = Tool.removeItem(_list, num0);
				maxIndex = _list.length -2;
				i = 0;
				continue;
			}
			i++;
		}
		return _list;
	}
	
	/**移除所以二个相同的。 */
	public static int [] removeBase2(int[] _list)
	{
		Arrays.sort(_list);
		if (_list.length< 3)
		{
			return _list;
		}
		int len = _list.length;
		int maxIndex = _list.length -1;
		for (int i = 0; i< maxIndex ;)
		{
			int num0 = _list[i];
			if ( _list[i+1] == num0)
			{
				_list = Tool.removeItem(_list, num0);
				_list = Tool.removeItem(_list, num0);
				maxIndex = _list.length -1;
				i = 0;
				continue;
			}
			i++;
		}
		return _list;
	}
	
	/**根据硬三嘴的规则剪掉  风字儿  （123） 组合
	 * */
	public static int [] remove123WithZui3(int [] _list)
	{
		int [] tmp = _list.clone();
		for (int i = 0; i < tmp.length ; )
		{
			int num0 = tmp[i] ;
			if (num0 < 400)
			{
				i++;
				continue;
			}
			int num1 = Tool.indexOf(tmp, num0 +1);
			int num2 = Tool.indexOf(tmp, num0 +2);
			int num3 = Tool.indexOf(tmp, num0 +3);
			if (num1 == -1 &&  num3 == -1 || num1 == -1 && num2 == -1 ||   num3==-1 && num2 == -1)
			{
				return tmp;
			}
			if (num1==-1)
			{
				num1 = tmp[num2];
				num2 = tmp[num3];
			}else if (num2 == -1)
			{
				num1 = tmp[num1];
				num2 = tmp[num3];
			}else
			{
				num1 = tmp[num1];
				num2 = tmp[num2];
			}
			tmp = Tool.removeItem(tmp, num0);
			tmp = Tool.removeItem(tmp, num1);
			tmp = Tool.removeItem(tmp, num2);
		}
		return tmp;
	}
	public static boolean sameType(int [] $list)
	{
		if ($list.length == 0)return true;
		int len = $list.length;
		int type = $list[0]/100;
		for (int i=1; i <len;i++ )
		{
			if($list[i] /100 != type )return false;
		}
		return true;
	}
	
	/**缺其中一个*/
	public static boolean lastOnefo(int []$list)
	{
		if ($list.length != 2)return false;
		int num0 = $list[0];
		/** 101 102  左边，中间 ，右边   相同*/
		int  [] inctace = {num0 - 1 ,num0 +1 , $list[1]+  1 , num0};
		
		for (int i  = 0 ; i < 4; i ++)
		{
			int tmpNum = inctace[i];
			int tUnit = tmpNum %100;
			if (tUnit == 0 || tUnit == 10)
			{
				continue;
			}
			int[] tPaiList = Tool.addItemToList($list, tmpNum);
			tPaiList = Tool.remove123(tPaiList);
			tPaiList = Tool.removeBase3(tPaiList);
			if (tPaiList.length == 0)
			{
				return true;
			}
		}
		return false;
	}
	
	/**缺其中一个  根据硬三嘴的规则 判断是否 3缺一*/
	public static boolean lastOnefoWithZui3(int []$list)
	{
		boolean baseL = lastOnefo($list);
		if (baseL) return true;
		//if ($list.length != 2)return false;
		
		int num0 = $list[0];
		if ( num0<400 || num0 >500)return false;
		/** 401 402 403 404  左边，中间 ，右边   相同 都可以*/
		return true;
	}
	
	public static int [] remove123456789(int $list[])
	{
		if ($list.length <9)return $list;
		int [] cList = $list.clone();
		int tType = $list[0] / 100;
		int type = tType;
		int num = $list[0];
		int comperCout = 1;
		$list = removeItem($list, num);
		for (int  j = 0 ;  j < $list.length;)
		{
			if($list[j] > 400){ j++; continue;};
			tType = $list[j] / 100;
			int tNum = $list[j];
			if ( tType == type &&num +1 == tNum)
			{
				$list = removeItem($list, tNum);
				num = tNum;
				j = 0;
				comperCout++;
				continue;
			}
			j++;
			//return $list;
		}
		if( comperCout < 9 )return cList;
		return $list;
	}
	/**获取列表中 数字的数量*/
	public static int  getNumberCount(int [] $list)
	{
		if ($list.length <=0)return 0;
		int tType = $list[0] / 100;
		int type = tType;
		int num = $list[0];
		int count = 0;
		for (int i = 0; i < $list.length;i++)
		{
			int tNum = $list[i];
			if (tNum > 400)return count ;
			if (type != tNum /100)return 0;
			count++;
		}
		return count;
	}
	/**获取类别中 字的数量*/
	public static int getZiCount(int [] $list)
	{
		if ($list.length <=0)return 0;
		int count = 0;
		for (int i = 0; i < $list.length;i++)
		{
			int tNum = $list[i];
			if (tNum < 400)continue ;
			count++;
		}
		return count;
	}
	/*public static int[] sublist(int[] $list,int formindex, int enIndex)
	{
		/*int[] tmp = new int[enIndex - formindex];
		int len = $list.length;
		int index = 0
		for (int i = formindex ; i < enIndex; i++)
		{
			if ()
			{
				tmp[index] = $list[i];
				index++;
			}
		}
		return tmp;
	}*/
	
	public static int [] removeLlist(int [] list ,int[] delete)
	{
		int [] cList = list.clone();
		for (int i = 0 ; i < delete.length; i ++)
		{
			if(indexOf(cList, delete[i])<-1)
			{	
				return list;
			}
			cList = removeItem(cList, delete[i]);
		}
		return cList;
	}
	

	/**
	 * int杞琤yte鏁扮粍
	 * @param i
	 * @return
	 */
	public static byte[] intToByteArray1(int i) {   
		byte[] result = new byte[4];   
		result[0] = (byte)((i >> 24) & 0xFF);
		result[1] = (byte)((i >> 16) & 0xFF);
		result[2] = (byte)((i >> 8) & 0xFF); 
		result[3] = (byte)(i & 0xFF);
		return result;
	}


}
