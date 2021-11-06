export class RandomUtil {

  /**
   * Create a list of n numbers with a specific mean and standard deviation
   * @param size the length of the list
   * @param mean the mean
   * @param sd the standard deviation
   */
  public static generate(size: number, mean: number, sd: number) {
    const emptyList = Array(...new Array(size));
    const randomList = emptyList.map(() => Math.random());
    const currentStats = RandomUtil.computeMeanSdAndIntervalRangeMinMax(randomList);
    return randomList.map(n => sd * (n - currentStats.mean) / currentStats.sd + mean);
  }

  /**
   * Compute mean, sd and the interval range: [min, max]
   * @param list the number list
   */
  public static computeMeanSdAndIntervalRangeMinMax(list) {
    const sum = list.reduce((a, b) => a + b, 0);
    const mean = sum / list.length;
    const sumMinusMean = list.reduce((a, b) => a + (b - mean) * (b - mean), 0);

    return {
      mean: mean,
      sd: Math.sqrt(sumMinusMean / (list.length - 1)),
      range: [Math.min(...list), Math.max(...list)]
    };
  }
}
