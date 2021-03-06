package qna.app.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

@Data //안에 get set이 다 들어와 있다 아래 코드대신 사용 가능
public class AppRequest {
	//==> requestFactory의 역할 -> request, response가 들어왔을때 utf, html이다.
	
	private HttpServletRequest req; // -->req로 줄인다
	private HttpServletResponse resp;
	
	//
	public String getReqUri() {
		return this.req.getRequestURI(); //uri를 가지고 온다
	}
	
	public String getMethod() {
		return this.req.getMethod(); //멧호드를 가지고 온다
	}
	
	
	//생서아를 만들어 준다
	public AppRequest(HttpServletRequest req, HttpServletResponse resp) {
		
		//req.setCharacterEncoding("UTF-8"); --> ctrl + 1 --> catch클릭 하면 아래처럼 나온다
		try {
			req.setCharacterEncoding("UTF-8"); // == 들어오는 문자열을 utf-8로 인코딩 해줘라
		} catch (UnsupportedEncodingException e) { //오류가 뜨게 되면
			// TODO Auto-generated catch block
			e.printStackTrace(); //왜 잘못 되었는지 알려줘라(타입이 잘못 되었는지....)
		} 
		
		resp.setCharacterEncoding("UTF-8"); //요청이 욌을때 응답도 utf-8로 해줘라
		resp.setContentType("text/html; charset=UTF-8");//text는 html이고 utf-8이라를 알려 준다
		
		this.req = req;
		this.resp = resp;
		
		
	}
	
	
	private void print(String msg) {
		
		try {
			resp.getWriter().append(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //-> 줄바꿈이 없다
		
	}
	
	private void println(String msg) {
		print(msg + "\r\n");
	} //-> 줄바꿈이 있다
	
	
	
	public void alterRedirect(String path, String msg) {
		println("<script>");  //자바 스크립트를 보여줄 수 았다>>
		println("alter('" + msg +"')");
		println("window.location.replace('" + path +"')");
		println("/<script>");
	}
	
	//둘을 합처주는 작업을 한것이다 아래의 두개
	public void render(String path) {
		
		//redirect로 시작하면
		if(path.startsWith("redirect:")) {
			
//			path.split("redirect:"); ctrl + 1
			String[] split = path.split("redirect:");
			redirectToJsp(split[1]);
		}else {
			forwardToJsp(path);
		}
	}
	
	
	//jsp로 보내주는 로직
	private void forwardToJsp(String path) {
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/" + path + ".jsp");
		// -->path만 입력해주면 이동하게 만들어 준다 자동으로
		
		
		//requestDispatcher.forward(req, resp); -> ctrl + 1 -> try, catch사용
		try {
			requestDispatcher.forward(req, resp); //요청과 응답을 항상 같이 보내주어야 한다??
		} catch (ServletException | IOException e) { //catch(Eception e)로 시용해도 가능 --> 모든 오류 상황을 고려
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//이걸 해주었기 때문에 questionController 에서 factory.forwardToJsp("usr/question/add");이렇게 사용이 가능하다.
	
	//아래의 작업은 위의 작업을 밖에서 뽑아 사용하기 위해서 만들어 준것이다.
	//여기 들어오는 값들을 사용하기 위해서 맨위에서 req, resp는 줄인말이다.
//	public HttpServletRequest getReq() {
//		return this.req;
//	}
//	
//	public HttpServletResponse getResp() {
//		return this.resp;
//	}    //== or 맨위에 @Data로 input하여 사용 가능하다
	
	
	private void redirectToJsp(String path) {
		
		//첫번째 화면에서 두번째 화면으로가서 질문하기를 누르면 아무 화면도
		//안나오는 상황에서 첫번째 화면으로 돌려주는 역할
		//resp.sendRedirect(path);
		try {
			resp.sendRedirect(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public <T> T getAttribute(String name, Class<T> type) {
		
		return type.cast(req.getParameter(name));
	}
	
	public void addAttribute(String name, Object object) {
		this.req.setAttribute(name, object);
	}
	
	
}
