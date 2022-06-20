import com.ruoyi.RuoYiApplication;
import com.ruoyi.system.service.ISysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

/**
 * @author zdw
 * @Date: 2022/5/24 14:34
 * @Description:
 */
@SpringBootTest(classes = RuoYiApplication.class)
public class UserTest {

    @Autowired
    private ISysUserService sysUserService;

    @Test
    public void testQuery() {
        sysUserService.selectUserList(null);
    }

    public static void main(String[] args) {
//        List<String> list = Arrays.asList("abc1", "abc2", "abc3");
//        long size = list.stream().skip(1)
//                .map(element -> element.substring(0, 3)).sorted().count();
//        System.out.println(size);

        int arr[] = {1, 3, 2, 5, 6, 900, 100};
        bubnleSort(arr);
        Arrays.stream(arr).forEach(e -> {
            System.out.println(e);
        });
    }


    //冒泡排序
    public static int[] bubnleSort(int arr[]) {
        int tmp = 0;
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }
        return arr;
    }


    //InsertSort
    public static int[] InsertSort(int arr[]) {
        
        int n =arr.length;
//        for (int i = 0; i < ; i++) {
//
//        }
        return arr;
    }

}
