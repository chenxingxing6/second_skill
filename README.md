## Hutool学习笔记

---
###### 1.构建树结构
```html
    List<TreeNode<String>> nodeList = CollUtil.newArrayList();
        nodeList.add(new TreeNode<>("1", "0", "系统管理", 5));
        nodeList.add(new TreeNode<>("11", "1", "用户管理", 222222));
        nodeList.add(new TreeNode<>("111", "11", "用户添加", 0));
        nodeList.add(new TreeNode<>("2", "0", "店铺管理", 1));
        nodeList.add(new TreeNode<>("21", "2", "商品管理", 44));
        nodeList.add(new TreeNode<>("221", "2", "商品管理2", 2));
        List<Tree<String>> build = TreeUtil.build(nodeList, "0");
        System.out.println(JSON.toJSONString(build));
```

---

```json
[
    {
        "id":"2",
        "parentId":"0",
        "weight":1,
        "name":"店铺管理",
        "children":[
            {
                "id":"221",
                "parentId":"2",
                "weight":2,
                "name":"商品管理2"
            },
            {
                "id":"21",
                "parentId":"2",
                "weight":44,
                "name":"商品管理"
            }
        ]
    }
]
```

---
###### 2.布隆过滤器
```html
public class BloomFilterTest {
    public static void main(String[] args) {
        int size = 1000;
        BitMapBloomFilter bitMapBloomFilter = new BitMapBloomFilter(size);
        for (int i = 0; i < size; i++) {
            bitMapBloomFilter.add(i + "");
        }
        boolean lxh = bitMapBloomFilter.contains("lxh");
        System.out.println(lxh);
    }
}

```

---

