package gui;

import org.springframework.web.bind.annotation.*;

import com.eds.order.*;
import com.eds.document.*;
import com.eds.filesystem.*;
import com.eds.common.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * REST Controller - 企业文档系统统一 API
 * 完整流程：创建订单 -> 审批 -> 生成文档 -> 保存 -> 遍历 -> 支付
 */
@RestController
@RequestMapping("/api")
public class DemoController {

    @PostMapping("/order/create")
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> params) {
        String output = captureOutput(() -> {
            String type = (String) params.getOrDefault("orderType", "simple");
            if ("composite".equals(type)) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) params.getOrDefault("items",
                        Arrays.asList(
                                new LinkedHashMap<String, Object>() {{ put("productName", "MacBook Pro"); put("amount", 3500); }},
                                new LinkedHashMap<String, Object>() {{ put("productName", "iPhone 15"); put("amount", 1200); }}
                        ));
                CompositeOrder composite = new CompositeOrder();
                for (Map<String, Object> item : items) {
                    String name = (String) item.getOrDefault("productName", "Product");
                    double amt = toDouble(item.getOrDefault("amount", 1000));
                    composite.add(new SimpleOrder(amt, name));
                }
                System.out.println("===== 创建组合订单 (组合模式) =====");
                composite.process();
                System.out.printf("订单总金额: $%.2f\n", composite.getAmount());
            } else {
                String productName = (String) params.getOrDefault("productName", "MacBook Pro");
                double amount = toDouble(params.getOrDefault("amount", 3500));
                System.out.println("===== 创建订单 (组合模式) =====");
                OrderComponent order = new SimpleOrder(amount, productName);
                order.process();
            }
        });

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("step", 1);
        result.put("pattern", "组合模式 (Composite)");
        result.put("output", output);
        return result;
    }

    @PostMapping("/order/approve")
    public Map<String, Object> approveOrder(@RequestBody Map<String, Object> params) {
        String output = captureOutput(() -> {
            OrderComponent order = buildOrder(params);
            System.out.println("===== 订单审批 (责任链模式) =====");
            System.out.println("审批链: 经理(金额<1000) -> 总监(金额<5000)");
            Approver manager = new ManagerApprover();
            Approver director = new DirectorApprover();
            manager.setSuccessor(director);
            manager.approve(order);
        });

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("step", 2);
        result.put("pattern", "责任链模式 (Chain of Responsibility)");
        result.put("output", output);
        return result;
    }

    @PostMapping("/order/document")
    public Map<String, Object> generateDocument(@RequestBody Map<String, Object> params) {
        String output = captureOutput(() -> {
            OrderComponent order = buildOrder(params);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            System.out.println("===== 生成订单文档 (组合模式 + 装饰器模式) =====");
            CompositeElement document = new CompositeElement();
            document.add(new TextElement("Order ID: " + timestamp));
            if (order instanceof SimpleOrder) {
                document.add(new TextElement("Product: " + ((SimpleOrder) order).getProductName()));
            } else {
                document.add(new TextElement("Type: 组合订单"));
            }
            document.add(new TextElement("Amount: $" + order.getAmount()));

            System.out.println("文档已生成 (" + document.getChildren().size() + " 个元素):");
            document.print();

            System.out.println("\n应用 BoldDecorator 装饰器:");
            BoldDecorator decorated = new BoldDecorator(document);
            decorated.print();
        });

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("step", 3);
        result.put("pattern", "组合模式 + 装饰器模式 (Decorator)");
        result.put("output", output);
        return result;
    }

    @PostMapping("/order/save")
    public Map<String, Object> saveDocument(@RequestBody Map<String, Object> params) {
        String output = captureOutput(() -> {
            OrderComponent order = buildOrder(params);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            CompositeElement document = new CompositeElement();
            document.add(new TextElement("Order ID: " + timestamp));
            document.add(new TextElement("Amount: $" + order.getAmount()));
            BoldDecorator decorated = new BoldDecorator(document);

            System.out.println("===== 保存文档 (策略模式) =====");
            String projectPath = System.getProperty("user.dir") + "/output/";
            new HtmlSaveStrategy(projectPath).save(decorated);
            System.out.println("保存路径: " + projectPath + "orders/");
        });

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("step", 4);
        result.put("pattern", "策略模式 (Strategy - HtmlSaveStrategy)");
        result.put("output", output);
        result.put("tree", buildFileTree());
        return result;
    }

    @PostMapping("/order/traverse")
    public Map<String, Object> traverseFileSystem(@RequestBody(required = false) Map<String, Object> params) {
        String output = captureOutput(() -> {
            System.out.println("===== 文件系统遍历 (迭代器模式 + 访问者模式) =====");
            String projectPath = System.getProperty("user.dir") + "/output/";

            Directory root = new Directory(projectPath, projectPath);
            Directory ordersDir = new Directory("orders", projectPath + "orders/");
            root.add(ordersDir);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            ordersDir.add(new File("order_summary_" + timestamp + ".txt",
                    "Order: " + timestamp + "\nAmount: $3500.00"));
            ordersDir.add(new File("order_history.txt", "历史订单记录"));

            Directory docsDir = new Directory("docs", projectPath + "docs/");
            root.add(docsDir);
            docsDir.add(new File("readme.txt", "项目说明文档"));

            FileSystemIterator iterator = new FileSystemIterator(root);
            Visitor statsVisitor = new StatsVisitor();

            while (iterator.hasNext()) {
                FileSystemNode node = iterator.next();
                node.accept(statsVisitor);
            }
            System.out.println("遍历完成");
        });

        List<Map<String, Object>> treeData = buildFileTree();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("step", 5);
        result.put("pattern", "迭代器模式 (Iterator) + 访问者模式 (Visitor)");
        result.put("output", output);
        result.put("tree", treeData);
        return result;
    }

    @PostMapping("/order/pay")
    public Map<String, Object> payOrder(@RequestBody Map<String, Object> params) {
        String output = captureOutput(() -> {
            OrderComponent order = buildOrder(params);
            String method = (String) params.getOrDefault("paymentMethod", "信用卡");
            String cardNumber = (String) params.getOrDefault("cardNumber", "4111-1111-1111-1111");

            System.out.println("===== 订单支付 (策略模式) =====");
            PaymentStrategy strategy;
            if ("支付宝".equals(method)) {
                strategy = new AlipayStrategy();
                System.out.println("选择策略: 支付宝支付");
            } else {
                strategy = new CreditCardStrategy(cardNumber);
                System.out.println("选择策略: 信用卡支付");
            }
            strategy.pay(order);
        });

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("step", 6);
        result.put("pattern", "策略模式 (Strategy - PaymentStrategy)");
        result.put("output", output);
        return result;
    }

    @PostMapping("/order/full")
    public Map<String, Object> fullFlow(@RequestBody Map<String, Object> params) {
        String output = captureOutput(() -> {
            String type = (String) params.getOrDefault("orderType", "simple");
            String productName = (String) params.getOrDefault("productName", "MacBook Pro");
            double amount = toDouble(params.getOrDefault("amount", 3500));
            String method = (String) params.getOrDefault("paymentMethod", "信用卡");
            String cardNumber = (String) params.getOrDefault("cardNumber", "4111-1111-1111-1111");
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            // Step 1: 创建订单
            System.out.println("===== Step 1: 创建订单 (组合模式) =====");
            OrderComponent order;
            if ("composite".equals(type)) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) params.getOrDefault("items", null);
                if (items != null && !items.isEmpty()) {
                    CompositeOrder composite = new CompositeOrder();
                    for (Map<String, Object> item : items) {
                        String name = (String) item.getOrDefault("productName", "Product");
                        double amt = toDouble(item.getOrDefault("amount", 1000));
                        composite.add(new SimpleOrder(amt, name));
                    }
                    order = composite;
                } else {
                    CompositeOrder composite = new CompositeOrder();
                    composite.add(new SimpleOrder(amount, productName));
                    composite.add(new SimpleOrder(1200, "iPhone 15"));
                    order = composite;
                }
            } else {
                order = new SimpleOrder(amount, productName);
            }
            order.process();

            // Step 2: 审批
            System.out.println("\n===== Step 2: 订单审批 (责任链模式) =====");
            System.out.println("审批链: 经理(金额<1000) -> 总监(金额<5000)");
            Approver manager = new ManagerApprover();
            Approver director = new DirectorApprover();
            manager.setSuccessor(director);
            manager.approve(order);

            // Step 3: 生成文档
            System.out.println("\n===== Step 3: 生成文档 (组合模式 + 装饰器模式) =====");
            CompositeElement document = new CompositeElement();
            document.add(new TextElement("Order ID: " + timestamp));
            document.add(new TextElement("Product: " + productName));
            document.add(new TextElement("Amount: $" + order.getAmount()));
            System.out.println("文档元素:");
            document.print();
            BoldDecorator decorated = new BoldDecorator(document);
            System.out.println("装饰后输出:");
            decorated.print();

            // Step 4: 保存
            System.out.println("\n===== Step 4: 保存文档 (策略模式) =====");
            String projectPath = System.getProperty("user.dir") + "/output/";
            new HtmlSaveStrategy(projectPath).save(decorated);

            // Step 5: 遍历
            System.out.println("\n===== Step 5: 文件系统遍历 (迭代器 + 访问者模式) =====");
            Directory root = new Directory(projectPath, projectPath);
            Directory ordersDir = new Directory("orders", projectPath + "orders/");
            root.add(ordersDir);
            ordersDir.add(new File("order_summary_" + timestamp + ".txt", document.getContent()));
            FileSystemIterator iterator = new FileSystemIterator(root);
            Visitor statsVisitor = new StatsVisitor();
            while (iterator.hasNext()) {
                iterator.next().accept(statsVisitor);
            }

            // Step 6: 支付
            System.out.println("\n===== Step 6: 订单支付 (策略模式) =====");
            PaymentStrategy strategy;
            if ("支付宝".equals(method)) {
                strategy = new AlipayStrategy();
                System.out.println("选择策略: 支付宝支付");
            } else {
                strategy = new CreditCardStrategy(cardNumber);
                System.out.println("选择策略: 信用卡支付");
            }
            strategy.pay(order);

            System.out.println("\n===== 全部流程执行完毕 =====");
        });

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("pattern", "组合 + 责任链 + 装饰器 + 策略 + 迭代器 + 访问者");
        result.put("output", output);
        result.put("tree", buildFileTree());
        return result;
    }

    // ==================== 工具方法 ====================

    private OrderComponent buildOrder(Map<String, Object> params) {
        String type = (String) params.getOrDefault("orderType", "simple");
        if ("composite".equals(type)) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) params.getOrDefault("items", null);
            CompositeOrder composite = new CompositeOrder();
            if (items != null) {
                for (Map<String, Object> item : items) {
                    String name = (String) item.getOrDefault("productName", "Product");
                    double amt = toDouble(item.getOrDefault("amount", 1000));
                    composite.add(new SimpleOrder(amt, name));
                }
            } else {
                composite.add(new SimpleOrder(toDouble(params.getOrDefault("amount", 3500)),
                        (String) params.getOrDefault("productName", "MacBook Pro")));
            }
            return composite;
        }
        return new SimpleOrder(
                toDouble(params.getOrDefault("amount", 3500)),
                (String) params.getOrDefault("productName", "MacBook Pro"));
    }

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;
        try {
            PrintStream captureStream = new PrintStream(baos, true, "UTF-8");
            System.setOut(captureStream);
            System.setErr(captureStream);
            action.run();
            captureStream.flush();
            return baos.toString("UTF-8");
        } catch (Exception e) {
            return "执行出错: " + e.getMessage();
        } finally {
            System.setOut(oldOut);
            System.setErr(oldErr);
        }
    }

    private double toDouble(Object value) {
        if (value instanceof Number) return ((Number) value).doubleValue();
        try { return Double.parseDouble(value.toString()); } catch (NumberFormatException e) { return 3500.0; }
    }

    private List<Map<String, Object>> buildFileTree() {
        List<Map<String, Object>> tree = new ArrayList<>();
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("name", "output/");
        root.put("type", "directory");
        List<Map<String, Object>> rootChildren = new ArrayList<>();

        Map<String, Object> orders = new LinkedHashMap<>();
        orders.put("name", "orders/");
        orders.put("type", "directory");
        List<Map<String, Object>> orderChildren = new ArrayList<>();
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        orderChildren.add(new LinkedHashMap<String, Object>() {{ put("name", "order_summary_" + ts + ".html"); put("type", "file"); }});
        orderChildren.add(new LinkedHashMap<String, Object>() {{ put("name", "order_summary_" + ts + ".txt"); put("type", "file"); }});
        orders.put("children", orderChildren);
        rootChildren.add(orders);

        Map<String, Object> docs = new LinkedHashMap<>();
        docs.put("name", "docs/");
        docs.put("type", "directory");
        List<Map<String, Object>> docChildren = new ArrayList<>();
        docChildren.add(new LinkedHashMap<String, Object>() {{ put("name", "readme.txt"); put("type", "file"); }});
        docs.put("children", docChildren);
        rootChildren.add(docs);

        root.put("children", rootChildren);
        tree.add(root);
        return tree;
    }
}
