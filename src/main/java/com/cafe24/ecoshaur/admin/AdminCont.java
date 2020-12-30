package com.cafe24.ecoshaur.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.cafe24.ecoshaur.admin.AdminDAO;

import net.utility.UploadSaveManager;
import net.utility.Utility;

@Controller
public class AdminCont {
  @Autowired
  private AdminDAO dao;

  public AdminCont() {
    System.out.println("---HomeCont()객체 생성됨...");
  }
  
//상세보기
 @RequestMapping(value = "admin/index.do", method = RequestMethod.GET)
 public ModelAndView index() {
   ModelAndView mav = new ModelAndView();
   mav.setViewName("admin/index");     
   
   // 총금액
   mav.addObject("total_price", dao.total_price());
   
   // 신규회원 수
   mav.addObject("new_user", dao.new_user());
   
   // 방문자 수
   mav.addObject("visitor", dao.visitor());
   
   // 거래 총 갯수
   mav.addObject("total_order", dao.total_order());
   
   // 최근 거래목록
   // Rental
   mav.addObject("Rlist", dao.Rental_list());
   // Order_history
   mav.addObject("Olist", dao.orderhistory_list());
   // Order_sheet -> id
   mav.addObject("IDlist", dao.ID_list());
   
   // top 포인트
   mav.addObject("top_point", dao.top_point());
   
   // 환불 TOP 순위
   mav.addObject("topFail_rental", dao.topFail_rental());
   mav.addObject("topFail_condition", dao.topFail_condition()); // Deposit 으로 받음
   
   // 카테고리별 요약목록
   mav.addObject("Category_info", dao.Category_info());
   return mav;
 }// read() end
  
  //최근 주문목록
  @RequestMapping(value = "admin/pages/newOrderList.do", method = RequestMethod.GET)
  public ModelAndView newOrderList() {
    ModelAndView mav = new ModelAndView();
    mav.setViewName("admin/pages/newOrderList");     
    
    // 최근 거래목록
    // Rental
    mav.addObject("Rlist", dao.Rental_list_t());
    // Order_history
    mav.addObject("Olist", dao.orderhistory_list_t());
    // Order_sheet -> id
    mav.addObject("IDlist", dao.ID_list_t());
    
    return mav;
  }
  
  //Top 포인트
  @RequestMapping(value = "admin/pages/top_point.do", method = RequestMethod.GET)
  public ModelAndView top_point() {
    ModelAndView mav = new ModelAndView();
    mav.setViewName("admin/pages/top_point");     
    
 // top 포인트
    mav.addObject("top_point", dao.top_point_t());
    
    return mav;
  }
  
  
  // top 환불 
  @RequestMapping(value = "admin/pages/BadTopUser.do", method = RequestMethod.GET)
  public ModelAndView badTopuser() {
    ModelAndView mav = new ModelAndView();
    mav.setViewName("admin/pages/BadTopUser");     
    
 // 환불 TOP 순위
    mav.addObject("topFail_rental", dao.topFail_rental_t());
    mav.addObject("topFail_condition", dao.topFail_condition_t()); // Deposit 으로 받음
    
    return mav;
  }
  
//회원 관리
 @RequestMapping(value = "admin/pages/member_list.do", method = RequestMethod.GET)
 public ModelAndView member_list() {
   ModelAndView mav = new ModelAndView();
   mav.setViewName("admin/pages/member_list");     
   
   // 회원목록 리스트
   mav.addObject("list", dao.member_list());
   
   return mav;
 }
 

  //회원 관리 수정
  @RequestMapping(value = "admin/pages/member_manager.do", method = RequestMethod.GET)
  public ModelAndView member_manager(String id) {
    ModelAndView mav = new ModelAndView();
    
    mav.setViewName("admin/pages/member_manager");
    
    mav.addObject("mymem", dao.member_read(id));
    return mav;
  }
  
  //회원 관리 수정
  @RequestMapping(value = "admin/pages/member_manager.do", method = RequestMethod.POST)
    public ModelAndView member_managerProc(MemberDTO dto, String old_pro_name, HttpServletRequest req) {
    ModelAndView mav = new ModelAndView();
    mav.setViewName("member/msgView");
    if(dto.getPro_name1().getSize() != 0) {
  //---------------------------------------------------------------------------
      String basePath = req.getRealPath("member/storage");
      MultipartFile pro_name1 = dto.getPro_name1();
      String poster = UploadSaveManager.saveFileSpring30(pro_name1, basePath);
      dto.setPro_name(poster);
    }
 //---------------------------------------------------------------------------
    else {
      dto.setPro_name(old_pro_name);
    }
    int cnt=dao.update(dto);
    if(cnt!=1) {  
        mav.addObject("msg1", "<script>alert('회원수정에 실패하였습니다'); window.location.href = 'member_list.do';</script>");
      } else {
        mav.addObject("msg1", "<script>alert('회원수정에 성공하였습니다'); window.location.href = 'member_list.do';</script>");
      }
    
    return mav;
  }
  
  
  // 회원 포인트
  @RequestMapping(value = "admin/pages/point.do", method = RequestMethod.GET)
  public ModelAndView member_point() {
    ModelAndView mav = new ModelAndView();
    
    mav.setViewName("admin/pages/point");
    
    //point
    mav.addObject("point", dao.point());
    // 회원목록 리스트
    mav.addObject("list", dao.member_list());
    
    return mav;
  }
  
  // 회원 포인트
  @RequestMapping(value = "admin/pages/point.do", method = RequestMethod.POST)
  public ModelAndView member_pointProc(PointDTO dto) {
    ModelAndView mav = new ModelAndView();
    mav.setViewName("admin/pages/msgView");
    
    int cnt = dao.point_insert(dto);
    
    if(cnt!=1) {  
      mav.addObject("msg1", "<script>alert('포인트증감에 실패하였습니다'); window.location.href = 'point.do';</script>");
      return mav;
    }
    else {
      mav.addObject("msg1", "<script>window.location.href = 'point.do';</script>");
      return mav;
    }
  }

  // 회원 좋아요/싫어요
  @RequestMapping(value = "admin/pages/rating.do", method = RequestMethod.GET)
  public ModelAndView member_rating() {
    ModelAndView mav = new ModelAndView();
    
    mav.setViewName("admin/pages/rating");
    
    //rating
    mav.addObject("rating", dao.rating());
    // 회원목록 리스트
    mav.addObject("list", dao.member_list());

    // Rental
    mav.addObject("Rlist", dao.rental_list());
    
    
    return mav;
  }
  
  
  // 회원 좋아요/싫어요
  @RequestMapping(value = "admin/pages/rating.do", method = RequestMethod.POST)
  public ModelAndView member_ratingProc(RatingDTO dto) {
    ModelAndView mav = new ModelAndView();
    mav.setViewName("admin/pages/msgView");
    
    dto.setId_receive(dao.rental_writer(dto.getNo()));
    int cnt = dao.rating_insert(dto);
    
    if(cnt!=1) {  
      mav.addObject("msg1", "<script>alert('평가입력에 실패하였습니다'); window.location.href = 'rating.do';</script>");
      return mav;
    }
    else {
      mav.addObject("msg1", "<script>window.location.href = 'rating.do';</script>");
      return mav;
    }
  }
  
  // 상품목록
  @RequestMapping(value = "admin/pages/rental_list.do", method = RequestMethod.GET)
  public ModelAndView rental_list() {
    ModelAndView mav = new ModelAndView();
    
    mav.setViewName("admin/pages/rental_list");
    
    // Rental
    mav.addObject("Rlist", dao.rental_list());
    
    
    return mav;
  }
  
  // 상품수정
  @RequestMapping(value = "admin/pages/rental_manager.do", method = RequestMethod.GET)
  public ModelAndView rental_manager(String product_no) {
    ModelAndView mav = new ModelAndView();
    
    mav.setViewName("admin/pages/rental_manager");
    
 // Rental
    mav.addObject("dto", dao.rental_read(product_no));
    
    RentalDTO dto = dao.rental_read(product_no);
    String reg_date = dto.getReg_date().substring(0, 10);
    mav.addObject("reg_date", reg_date);
    String code = dao.rental_code(product_no);
    String minor = dao.category_minor(code);
    mav.addObject("select_minor", minor);
    mav.addObject("minor", dao.MNcategory());
    return mav;
  }
  
//상품수정
 @RequestMapping(value = "admin/pages/rental_manager.do", method = RequestMethod.POST)
 public ModelAndView rental_managerProc(RentalDTO dto, String old_thmb_name, String old_image_name, String ctCode, HttpServletRequest req) {
   ModelAndView mav = new ModelAndView();
   mav.setViewName("member/msgView");
   
   // 상품소분류 -> 상품코드 변환 -> dto에 세팅
   String code = dao.category_code(ctCode);
   dto.setCategory_code(code);
   
//-----------------------------------------------------------------------------------
   // 썸네일파일 이름 저장
   if(dto.getPosterMF().getSize() != 0) {
     String basePath = req.getRealPath("category/storage");
     MultipartFile PosterMF = dto.getPosterMF();
     String poster = UploadSaveManager.saveFileSpring30(PosterMF, basePath);
     dto.setThmb_name(poster);
   }
   else {
     dto.setThmb_name(old_thmb_name);
   }
   
   // 이미지파일 이름 저장 
   if(dto.getFilenameMF().getSize() != 0) {
     String basePath = req.getRealPath("category/storage");
     MultipartFile filenameMF = dto.getFilenameMF();
     String poster = UploadSaveManager.saveFileSpring30(filenameMF, basePath);
     dto.setImage_name(poster);
   }
//---------------------------------------------------------------------------
   else {
     dto.setImage_name(old_image_name);
   }
   String saveDirectory = Utility.getRoot();
   
   
   int cnt=dao.rental_update(dto, saveDirectory);
   if(cnt!=1) {  
       mav.addObject("msg1", "<script>alert('상품수정에 실패하였습니다'); window.location.href = 'rental_list.do';</script>");
     } else {
       mav.addObject("msg1", "<script>alert('상품수정에 성공하였습니다'); window.location.href = 'rental_list.do';</script>");
     }
   
   return mav;
 }
  
  
//주문서 목록
@RequestMapping(value = "admin/pages/order_list.do", method = RequestMethod.GET)
public ModelAndView order_list() {
  ModelAndView mav = new ModelAndView();
  mav.setViewName("admin/pages/order_list");     
  
  // 회원목록 리스트
  mav.addObject("list", dao.Order_list());
  
  return mav;
}

//주문서 목록 수정 
@RequestMapping(value = "admin/pages/order_manager.do", method = RequestMethod.GET)
public ModelAndView order_manager(String order_no) {
  ModelAndView mav = new ModelAndView();
  
  mav.setViewName("admin/pages/order_manager");
  
  mav.addObject("ord", dao.order_read(order_no));
  return mav;
}

//주문서 목록 수정
@RequestMapping(value = "admin/pages/order_manager.do", method = RequestMethod.POST)
  public ModelAndView Order_manager(OrderDTO dto, HttpServletRequest req) {
  ModelAndView mav = new ModelAndView();
  mav.setViewName("member/msgView");

  int cnt=dao.order_update(dto);
  if(cnt!=1) {  
      mav.addObject("msg1", "<script>alert('주문서 목록 수정에 실패하였습니다'); window.location.href = 'order_list.do';</script>");
    } else {
      mav.addObject("msg1", "<script>alert('주문서 목록 수정에 성공하였습니다'); window.location.href = 'order_list.do';</script>");
    }
  
  return mav;
}
  
//주문내역서 목록
@RequestMapping(value = "admin/pages/orderh_list.do", method = RequestMethod.GET)
public ModelAndView orderh_list() {
ModelAndView mav = new ModelAndView();
mav.setViewName("admin/pages/orderh_list");
//주문내역서 목록 리스트                                                                     
mav.addObject("list", dao.Orderh_list());
return mav;     
}
//주문내역서 목록 수정 
@RequestMapping(value = "admin/pages/orderh_manager.do", method = RequestMethod.GET)
public ModelAndView orderh_manager(String order_detail_no) {
ModelAndView mav = new ModelAndView();

mav.setViewName("admin/pages/orderh_manager");

mav.addObject("ordh", dao.orderh_read(order_detail_no));
return mav;
}

//주문내역서 목록 수정
@RequestMapping(value = "admin/pages/orderh_manager.do", method = RequestMethod.POST)
public ModelAndView Orderh_manager(OrderHistoryDTO dto, HttpServletRequest req) {
ModelAndView mav = new ModelAndView();
mav.setViewName("member/msgView");

int cnt=dao.orderh_update(dto);
if(cnt!=1) {  
  mav.addObject("msg1", "<script>alert('주문내역서 목록 수정에 실패하였습니다'); window.location.href = 'orderh_list.do';</script>");
} else {
  mav.addObject("msg1", "<script>alert('주문내역서 목록 수정에 성공하였습니다'); window.location.href = 'orderh_list.do';</script>");
}

return mav;
}
  
  
  
  
}
