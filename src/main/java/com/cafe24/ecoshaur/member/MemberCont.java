package com.cafe24.ecoshaur.member;

import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.cafe24.ecoshaur.category.RentalDTO;

import net.utility.UploadSaveManager;
import net.utility.Utility;

@Controller
public class MemberCont {
  
  @Autowired
  private MemberDAO dao;
  @Autowired
  private JavaMailSender mailSender;
  
  public MemberCont() {
    System.out.println("---MemberCont()객체 생성됨");
  }  
   
//로그인
  @RequestMapping(value="login.do", method=RequestMethod.GET)
  public ModelAndView loginForm(MemberDTO dto) {
    ModelAndView mav = new ModelAndView();
    mav.setViewName("member/loginForm");
    mav.addObject("root", Utility.getRoot());
 
    return mav;
  }//createForm() end
  
  
  	//로그인 결과
  @RequestMapping(value = "loginProc.do", method = RequestMethod.POST)
	public ModelAndView loginProc(MemberDTO dto, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		String grade = dao.loginProc(dto);
		HttpSession session = request.getSession();
		String id=null;
		if (grade != null) {
			mav.setViewName("member/loginProc");
			id=dto.getId();
			String pro_name = dao.getProname(id);
			session.setAttribute("id", id);
			session.setAttribute("grade", grade);
			session.setAttribute("pro_name", pro_name);
			// 최근로그인 업데이트
			dao.lastdate(id);
		}else {
			mav.setViewName("member/msgView");
			mav.addObject("msg1", "<script>alert('로그인에 실패하였습니다'); window.history.back();</script>");
		}
		
		return mav;
	}
  
	//로그아웃
	@RequestMapping(value = "logout.do", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();

		mav.setViewName("member/logout");
		session.invalidate();

		return mav;
	}
  
  //약관동의
  @RequestMapping(value="agree.do", method=RequestMethod.GET)
  public ModelAndView agreement(HttpSession session) {
    ModelAndView mav = new ModelAndView();
    mav.setViewName("member/agreement");
    mav.addObject("root", Utility.getRoot());
 
    return mav;
  }//createForm() end
  
  
//쿠키를 활용하여 아이디 중복확인을 해야만 회원가입 한다
 @RequestMapping("memberForm.do")
 public void idCheckProc3(HttpServletRequest req, HttpServletResponse resp) {
	 String uid=req.getParameter("uid");
	 
	 if(uid != null) {
		 try{
			 int cnt=dao.duplicateID(uid);
		     System.out.println("asd");
		     //1)text응답-----------------------
		     /*
		     resp.setContentType("text/plain; charset=UTF-8");
		     PrintWriter out=resp.getWriter();
		     out.println(cnt);
		     out.flush();
		     out.close();
		     */ 
		     
		     //2)json응답-----------------------
		     // https://mvnrepository.com에서
		     // json-simple검색후 
		     // pom.xml에 의존성 추가해야 함
		     JSONObject json=new JSONObject();
		     //json.put(key, value)
		     json.put("count", cnt);
		     resp.setContentType("text/plain; charset=UTF-8");
		     PrintWriter out=resp.getWriter();
		     out.println(json.toString());
		     out.flush();
		     out.close();
    
		 }catch (Exception e) {
		     System.out.println("아이디중복확인쿠키실패:"+e);
		   }//try end
	 }

   
 }//idCheckProc3() end
   
  //회원가입
  @RequestMapping(value="memberForm.do", method=RequestMethod.GET)
  public ModelAndView memberForm(MemberDTO dto, HttpServletRequest req, HttpSession session) throws Exception{
	  ModelAndView mav=new ModelAndView();
	  mav.setViewName("member/memberForm");
	  mav.addObject("root", Utility.getRoot());
	  
	  return mav;
  }//memberForm() end
  
//회원가입 결과
  @RequestMapping(value="memberProc.do", method=RequestMethod.POST)
  public ModelAndView memberProc(MemberDTO dto, HttpServletRequest req, HttpSession session) throws Exception {
	  ModelAndView mav = new ModelAndView();
	  
	  mav.addObject("check", dao.insertmember(dto));
	  
	  mav.setViewName("member/memberProc");
	  return mav;
  }
  
  //회원정보 보기
  @RequestMapping(value="mypage.do", method=RequestMethod.GET)
  public ModelAndView mypage(HttpServletRequest request, HttpSession session ){
	  ModelAndView mav = new ModelAndView();
	  String id=(String) session.getAttribute("id");
	  mav.addObject("mymem", dao.read(id));
	  mav.addObject("mypage", dao.mypage(id));
	  mav.addObject("order_sheet", dao.order_sheet(id));
	  mav.addObject("board_list", dao.board_list(id));
	  mav.addObject("qna_list", dao.qna_list(id));
	  mav.addObject("point_p", dao.point_p(id));
	  mav.addObject("point_m", dao.point_m(id));
	  mav.addObject("rating_good", dao.rating_good(id));
	  mav.addObject("rating_bad", dao.rating_bad(id));
	   
	  mav.setViewName("member/mypage");
	  return mav;
  }
  
  	//회원수정
	@RequestMapping(value="ModifyForm.do", method=RequestMethod.GET)
	  public ModelAndView ModifyForm(String id) {
	    ModelAndView mav = new ModelAndView();
	    mav.setViewName("member/ModifyForm");
	    mav.addObject("mymem", dao.read(id));
	    return mav;
	  }//ModifyForm() end
	  
	
	  //회원수정
	  @RequestMapping(value="ModifyProc.do", method=RequestMethod.POST)
	  public ModelAndView ModifyProc(MemberDTO dto ) {
	    ModelAndView mav = new ModelAndView();
	    mav.setViewName("member/msgView");
	    int cnt=dao.update(dto);
	    if(cnt!=1) { 
	        mav.addObject("msg1", "<script>alert('회원수정에 실패하였습니다'); window.location.href = '../login.do';</script>");
	      } else {
	        mav.addObject("msg1", "<script>alert('회원수정에 성공하였습니다'); window.location.href = '../login.do';</script>");
	      }
	    
	    return mav;
	  }//ModifyProc() end
	  
	//회원탈퇴
		@RequestMapping(value="delete.do", method=RequestMethod.GET)
		public ModelAndView deleteForm(HttpSession session) {
		      ModelAndView mav = new ModelAndView();
		      mav.setViewName("member/memberdel");

		      return mav;
		  }//deleteForm() end

		//회원탈퇴
		@RequestMapping(value="deleteProc.do", method=RequestMethod.GET)
		  public ModelAndView deleteProc(HttpSession session) {
		      ModelAndView mav = new ModelAndView();
		      mav.setViewName("member/memberdel");
		      String id = (String) session.getAttribute("id");
		      int cnt = dao.delete(id);
		      if(cnt!=1) {
		        mav.addObject("msg1", "<script>alert('회원탈퇴에 실패하였습니다'); window.location.href = 'login.do';</script>");
		      } else {
		        mav.addObject("msg1", "<script>alert('회원탈퇴에 성공하였습니다'); window.location.href = 'login.do';</script>");
		      }
		      return mav;
		  }//deleteProc() end
  
		///비밀번호 찾기
		  @RequestMapping(value="findpw.do", method=RequestMethod.GET)
		  public ModelAndView findPw(MemberDTO dto, HttpServletRequest req) throws Exception {
			  ModelAndView mav = new ModelAndView();
			  
			  mav.setViewName("member/findPw");
			  return mav;
			  
		  }
		  
		///비밀번호 찾기
		  @RequestMapping(value="findPwProc.do", method=RequestMethod.POST)
		  public ModelAndView findPwProc(MemberDTO dto) {
			  ModelAndView mav = new ModelAndView();
			  
			  int cnt = dao.findPw(dto.getId(), dto.getEmail());
			  System.out.println(cnt);
			  if(cnt==1) {
				  mav.setViewName("member/findPwProc");
				  
				  String setfrom = "ecoshaur@gmail.com";       
				  String tomail  = dto.getEmail();     // 받는 사람 이메일
				  String title	 ="임시 비밀번호입니다";  
				  String content =null;
				  	try {
				  	  MimeMessage message = mailSender.createMimeMessage();
				      MimeMessageHelper messageHelper 
				                        = new MimeMessageHelper(message, true, "UTF-8");
				      
				    //-------------------------임시비밀번호 만들고 DB수정하기---------------------------------------
						String pw = "";
						char[] charSet = new char[] { 	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
														'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
														'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
														'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
														'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
														'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
														'y', 'z'};

						for (int i = 0; i < 10; i++) {
							int index = (int)((Math.random() * charSet.length));
							pw += charSet[index];
						}
						
						int cnt2 = dao.memberSamplepasswd(dto.getId(), dto.getEmail(), pw);
						if(cnt2 != 0){
				//-------------------------------이메일 보내기----------------------------------------------
							content="회원님이 요청하신 비밀번호는 " + pw + " 입니다";	
						    messageHelper.setFrom(setfrom);  // 보내는사람 생략하거나 하면 정상작동을 안함
						    messageHelper.setTo(tomail);     // 받는사람 이메일
						    messageHelper.setSubject(title); // 메일제목은 생략이 가능하다
						    messageHelper.setText(content);  // 메일 내용
						     
						    mailSender.send(message);
						}
					} catch(Exception e){
					   System.out.println(e);
					}

				  }
			  else {
				  mav.setViewName("member/msgView");
			      mav.addObject("msg1", "<script>alert('비밀번호찾기에 실패하였습니다'); window.location.href = './';</script>");
			  }
		 
		  	return mav;
		  }
		  
		  

	    //주문내역서 보기
	      @RequestMapping(value="order_history.do", method=RequestMethod.GET)
	      public ModelAndView order_history(String order_no, HttpSession session){
	        ModelAndView mav = new ModelAndView();
	        String id=(String) session.getAttribute("id");
	        
	        mav.addObject("member", dao.read(id));
	        mav.addObject("order", dao.order_sheetRead(order_no));
	        ArrayList<OrderHistoryDTO> dto = new ArrayList<OrderHistoryDTO>();
	        dto = dao.order_history(order_no);
	        mav.addObject("dto", dto);
	        ArrayList<RentalDTO> rdto = new ArrayList<RentalDTO>();
	        rdto = dao.Rental_list(order_no);
	        mav.addObject("Rdto", rdto);
	        
	        
	        // 총 일일대여료, 총 보증금 계산, 총 금액
	        int daily_total = 0;
	        int deposit_total = 0;
	        int total = 0;
	        for(int i=0;i<rdto.size();i++) {
	          daily_total += rdto.get(i).getPrice_daily() * dto.get(i).getQuantity() * dto.get(i).getRental_period();
	          deposit_total += rdto.get(i).getDeposit();
	          total += (rdto.get(i).getPrice_daily() * dto.get(i).getQuantity() * dto.get(i).getRental_period()) + rdto.get(i).getDeposit();
	        }
	        
	        
	        // 사용한 포인트 계산
	        int real_total = 0;
	        for(int i=0;i<dto.size();i++) {
	          real_total += dto.get(i).getTotal_price();
	        }
	        int point = real_total - total;
	        
	        
	        
	        mav.addObject("daily_total", daily_total);
	        mav.addObject("deposit_total", deposit_total);
	        mav.addObject("point", point);
	        mav.addObject("total", real_total);
	        
	        mav.setViewName("member/order_history");
	        return mav;
	      }
	      
	      
	    //유저정보 보기
	      @RequestMapping(value="Userpage.do", method=RequestMethod.GET)
	      public ModelAndView userpage(String id){
	        ModelAndView mav = new ModelAndView();
	        mav.addObject("root", Utility.getRoot());
	        mav.addObject("dto", dao.userpage(id));
	        mav.addObject("user_rental", dao.user_rental(id));
	        mav.addObject("user_board", dao.user_board(id));
	        mav.setViewName("member/userpage");
	        return mav;
	      }//userpage() end
		  
}//class end
