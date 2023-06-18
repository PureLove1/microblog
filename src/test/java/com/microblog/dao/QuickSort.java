package com.microblog.dao;

/**
 * @author 贺畅
 * @date 2023/3/28
 */
public class QuickSort {
	public static void main(String[] args) {
		int[] a = new int[]{100, 3, 10, 5, 2, 425, 3, 324, 346, 4, 23, 365, 34};
		int[] ints = quickSort(a, 0, a.length - 1);
		for (int i = 0; i < ints.length; i++) {
			System.out.print(ints[i]+",");
		}
	}

	private static int[] quickSort(int[] array, int low, int high) {
		int start = low;
		int end = high;
		int flag = array[low];//选取基准点
		while (low < high) {
			while (low < high && array[high] >= flag)
				high--;
			int temp = array[low];
			array[low] = array[high];
			array[high] = temp;
			while (low < high && array[low] <= flag)
				low++;
			temp = array[high];
			array[high] = array[low];
			array[low] = temp;
		}
		if (low-1 > start) quickSort(array, start, low-1);
		if (end > low + 1) quickSort(array, low + 1, end);
		return array;
	}
}
