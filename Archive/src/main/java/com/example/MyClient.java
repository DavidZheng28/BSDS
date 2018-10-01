//package com.example;
//
//import static com.example.Main.BASE_URI;
//
//import javax.ws.rs.client.*;
//import javax.ws.rs.core.*;
//
//public class MyClient {
//
//  public static void main(String[] argv) {
//    // Please, do not remove this line from file template, here invocation of web service will be inserted
//    Client client = ClientBuilder.newClient();
//    WebTarget baseTarget = client.target(BASE_URI);
//    //3.1 get请求；如果2是根路径，可以同path方法添加下级连接。需要queryString参数可以通过queryParam实现
//
//    WebTarget subTarget = baseTarget.path("s").queryParam("ie", "utf-8").queryParam("wd", "jersey");
//
//    //3.2 post 请求；通过Invocation.Builder.post(Enity.entity());传递form表单；form表单通过javax.ws.rs.core.Form类型创建实例
//    //4.接受response返回类型。这里如果需要特别指明可以通过WebTarget.request方法定义media返回类型。Jersey提供javax.ws.rs.core.MediaType类选择。
//    //Invocation.Builder builder = subTarget.request(MediaType.APPLICATION_JSON_TYPE).header("someHead", "true");
//    Invocation.Builder builder = subTarget.request().header("someHead", "true");
//
//    Form form = new Form();
//    form.param("ie", "utf-8");
//    form.param("wd", "jersey");
//    Response response = builder.post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));
//
////    5. 设置请求参数，例如头，cookie等
//
////    6.发送请求，并且接受返回。这里是堵塞请求，必须等返回response，并操作返回值
//
////    Response response_get = builder.get();
//
//    System.out.println("request url=" + subTarget.getUri().toString());
//    if (response.getStatus() == 200) {
//      System.out.println("status=" + response.getStatus() + ", statusInfo=" + response
//          .getStatusInfo());      //System.out.println(response.readEntity(String.class));					}
//    }
//
//  }
//}
