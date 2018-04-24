package com.vietlh.wethoong;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.vietlh.wethoong.entities.Dieukhoan;
import com.vietlh.wethoong.utils.DBConnection;
import com.vietlh.wethoong.utils.Queries;
import com.vietlh.wethoong.utils.UtilsHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    private Queries queries = new Queries(DBConnection.getInstance(this));
    private UtilsHelper helper = new UtilsHelper();
    private Dieukhoan dieukhoan;
    private String dieukhoanId;
    private ArrayList<String> vanbanid = new ArrayList<>();

    private ScrollView scrollView;
    private TextView lblVanban;
    private TextView lblDieukhoan;
    private Button btnBreadscrubs;
    private TextView lblNoidung;
    private LinearLayout extraView;
    private LinearLayout mucphatView;
    private TextView mucphatDetails;
    private LinearLayout phuongtienView;
    private TextView phuongtienDetails;
    private LinearLayout linhvucView;
    private TextView linhvucDetails;
    private LinearLayout doituongView;
    private TextView doituongDetails;
    private LinearLayout hinhphatbosung;
    private TextView hinhphatbosungDetails;
    private LinearLayout bienphapkhacphuc;
    private TextView bienphapkhacphucDetails;
    private LinearLayout tamgiu;
    private TextView tamgiuDetails;
    private LinearLayout thamquyen;
    private TextView thamquyenDetails;
    private LinearLayout minhhoaView;
    private LinearLayout childrenDieukhoan;
    private TableLayout tblChildrenDieukhoan;

    private ConstraintLayout btnXemthemView;
    private Button btnXemthem;

    private ConstraintLayout adsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getPassingParameters();
        initComponents();
        dieukhoan = queries.searchDieukhoanByID(dieukhoanId,vanbanid).get(0);

        if(relatedChildren.count>0){
            lblSeeMore.isEnabled = true
            consLblSeeMoreHeight.constant = 50
        }else{
            lblSeeMore.isEnabled = false
            consLblSeeMoreHeight.constant = 0
        }
        showDieukhoan()

        tblView.reloadData()
        updateTableViewHeight()
        initAds()
    }

    private void getPassingParameters(){
        dieukhoanId = (String) getIntent().getStringExtra("dieukhoanId");
    }

    private void initComponents(){
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        btnXemthemView = (ConstraintLayout)findViewById(R.id.btnXemthemView);
        adsView = (ConstraintLayout)findViewById(R.id.adsView);

        lblVanban = (TextView)findViewById(R.id.lblVanban);
        lblDieukhoan = (TextView)findViewById(R.id.lblDieukhoan);
        lblNoidung = (TextView)findViewById(R.id.lblNoidung);
        mucphatDetails = (TextView)findViewById(R.id.mucphatDetails);
        phuongtienDetails = (TextView)findViewById(R.id.phuongtienDetails);
        linhvucDetails = (TextView)findViewById(R.id.linhvucDetails);
        doituongDetails = (TextView)findViewById(R.id.doituongDetails);
        hinhphatbosungDetails = (TextView)findViewById(R.id.hinhphatbosungDetails);
        bienphapkhacphucDetails = (TextView)findViewById(R.id.bienphapkhacphucDetails);
        tamgiuDetails = (TextView)findViewById(R.id.tamgiuDetails);
        thamquyenDetails = (TextView)findViewById(R.id.thamquyenDetails);

        btnXemthem = (Button)findViewById(R.id.btnXemthem);
        btnBreadscrubs = (Button)findViewById(R.id.btnBreadscrubs);

        extraView = (LinearLayout)findViewById(R.id.extraView);
        mucphatView = (LinearLayout)findViewById(R.id.mucphatView);
        phuongtienView = (LinearLayout)findViewById(R.id.phuongtienView);
        linhvucView = (LinearLayout)findViewById(R.id.linhvucView);
        doituongView = (LinearLayout)findViewById(R.id.doituongView);
        hinhphatbosung = (LinearLayout)findViewById(R.id.hinhphatbosung);
        bienphapkhacphuc = (LinearLayout)findViewById(R.id.bienphapkhacphuc);
        tamgiu = (LinearLayout)findViewById(R.id.tamgiu);
        thamquyen = (LinearLayout)findViewById(R.id.thamquyen);
        minhhoaView = (LinearLayout)findViewById(R.id.minhhoaView);
        childrenDieukhoan = (LinearLayout)findViewById(R.id.childrenDieukhoan);

        tblChildrenDieukhoan = (TableLayout)findViewById(R.id.tblChildrenDieukhoan);
    }

    func updateTableViewHeight() {
        consHeightTblView.constant = 50000
        tblView.reloadData()
        tblView.layoutIfNeeded()

        var tableHeight:CGFloat = 0
        for obj in tblView.visibleCells {
            if let cell = obj as? UITableViewCell {
                tableHeight += cell.bounds.height
            }
        }
        consHeightTblView.constant = tableHeight
        tblView.sizeToFit()
        tblView.layoutIfNeeded()
    }

    //TODO: Init Google Admob
//    func initAds() {
//        if GeneralSettings.isAdEnabled && AdsHelper.isConnectedToNetwork() {
//            bannerView = GADBannerView(adSize: kGADAdSizeSmartBannerPortrait)
//            AdsHelper.addBannerViewToView(bannerView: bannerView,toView: viewAds, root: self)
//        }else{
//            btnFBBanner.addTarget(self, action: #selector(btnFouderFBAction), for: .touchDown)
//            AdsHelper.addButtonToView(btnFBBanner: btnFBBanner, toView: viewAds)
//        }
//    }

    //TODO: Handle internet connection off for adsView
//    func btnFouderFBAction() {
//        let url = URL(string: GeneralSettings.getFBLink)
//        if UIApplication.shared.canOpenURL(url!) {
//            if #available(iOS 10.0, *) {
//                UIApplication.shared.open(url!, options: [:], completionHandler: nil)
//            } else {
//                UIApplication.shared.openURL(url!)
//            }
//        }
//    }

    func updateDetails(dieukhoan: Dieukhoan) {
        self.dieukhoan = dieukhoan
        specificVanbanId.append( String(describing:dieukhoan.getVanban().getId()))

        for child in getChildren(keyword: String(describing: dieukhoan.id)) {
            children.append(child)
        }
        rowCount = children.count

        for child in Queries.getAllDirectRelatedDieukhoan(dieukhoanId: dieukhoan.getId()) {
            relatedChildren.append(child)
        }

        for child in Queries.getAllRelativeRelatedDieukhoan(dieukhoanId: dieukhoan.getId()) {
            relatedChildren.append(child)
        }

        hinhphatbosungList = Queries.getAllHinhphatbosung(dieukhoanId: dieukhoan.getId())
        bienphapkhacphucList = Queries.getAllBienphapkhacphuc(dieukhoanId: dieukhoan.getId())
        tamgiuphuongtienList = getTamgiuPhuongtienList()
        thamquyenList = getThamquyenList()

        for parent in getParent(keyword: String(describing: dieukhoan.cha)) {
            parentDieukhoan = parent
        }

    }


    func hideMinhhoaStackview(isHidden: Bool)  {
        consSvStackviewHeightSmall.constant = 0
        if(isHidden){
            svStackview.isHidden = true
            consSvStackviewHeightSmall.isActive = true
        }else{
            svStackview.isHidden = false
            consSvStackviewHeightSmall.isActive = false
        }
    }

    func hideMinhhoaView(isHidden: Bool) {
        consViewMinhhoaHeight.constant = 0
        if isHidden {
            consViewMinhhoaHeight.isActive = true
        }else {
            consViewMinhhoaHeight.isActive = false
        }
    }

    func hideExtraInfoView(isHidden: Bool)  {
        if(isHidden){
            consExtraViewHeight.constant = 0
            consExtraViewHeight.isActive = true
            populateExtraInfoView()
            viewExtraInfo.isHidden = true
        }else{
            consExtraViewHeight.isActive = false
            viewExtraInfo.isHidden = false
        }
    }

    func hideBosungKhacphucView(isHidden: Bool)  {
        if(isHidden){
            consViewHinhphatbosungHeight.isActive = true
            consViewBienphapkhacphucHeight.isActive = true
            consViewTamgiuPhuongtienHeight.isActive = true
            consViewThamquyenHeight.isActive = true
            consViewBosungKhacphucHeight.constant = 0
            consViewBosungKhacphucHeight.isActive = true
            viewBosungKhacphuc.isHidden = true
        }else{
            consViewBosungKhacphucHeight.isActive = false
            consViewHinhphatbosungHeight.isActive = false
            consViewBienphapkhacphucHeight.isActive = false
            consViewTamgiuPhuongtienHeight.isActive = false
            consViewThamquyenHeight.isActive = false
            viewBosungKhacphuc.isHidden = false
        }
    }

    func hideHinhphatbosungView(isHidden: Bool)  {
        if(isHidden){
            consViewHinhphatbosungHeight.constant = 0
            consViewHinhphatbosungHeight.isActive = true
        }else{
            consViewBosungKhacphucHeight.isActive = false
            consViewHinhphatbosungHeight.isActive = false
            viewBosungKhacphuc.isHidden = false
        }
    }

    func hideBienphapkhacphucView(isHidden: Bool)  {
        if(isHidden){
            consViewBienphapkhacphucHeight.constant = 0
            consViewBienphapkhacphucHeight.isActive = true
        }else{
            consViewBosungKhacphucHeight.isActive = false
            consViewBienphapkhacphucHeight.isActive = false
            viewBosungKhacphuc.isHidden = false
        }
    }

    func hideTamgiuPhuongtienView(isHidden: Bool)  {
        if(isHidden){
            consViewTamgiuPhuongtienHeight.constant = 0
            consViewTamgiuPhuongtienHeight.isActive = true
        }else{
            consViewBosungKhacphucHeight.isActive = false
            consViewTamgiuPhuongtienHeight.isActive = false
            viewBosungKhacphuc.isHidden = false
        }
    }

    func hideThamquyenView(isHidden: Bool)  {
        if(isHidden){
            consViewThamquyenHeight.constant = 0
            consViewThamquyenHeight.isActive = true
        }else{
            consViewBosungKhacphucHeight.isActive = false
            consViewThamquyenHeight.isActive = false
            viewBosungKhacphuc.isHidden = false
        }
    }

    func showDieukhoan() {
        lblVanban.text = dieukhoan!.getVanban().getMa()
        lblDieukhoan.text = dieukhoan!.getSo()
        let breadscrubText = search.getAncestersNumber(dieukhoan: dieukhoan!, vanbanId: [String(describing: dieukhoan!.getVanban().getId())])
        if breadscrubText.characters.count > 0 {
            btnParentBreadscrub.setTitle(breadscrubText, for: .normal)
            btnParentBreadscrub.isEnabled = true
        }else {
            btnParentBreadscrub.setTitle("", for: .normal)
            btnParentBreadscrub.isEnabled = false
        }

        let noidung = "\(String(describing: dieukhoan!.getTieude())) \n \(String(describing: dieukhoan!.getNoidung()))"
        lblNoidung.text = noidung

        images = dieukhoan!.getMinhhoa()

        if(images.count > 0){
            fillMinhhoaToViewMinhhoa(images: images)
        }else{
            hideMinhhoaView(isHidden: true)
        }

        // Enable extra section for details of ND46
        if String(describing:dieukhoan!.vanban.getId()) == GeneralSettings.getVanbanInfo(name: "ND46", info: "id") {
            hideExtraInfoView(isHidden: false)
            populateExtraInfoView()
        }else{
            hideExtraInfoView(isHidden: true)
        }
        if hinhphatbosungList.count < 1 && bienphapkhacphucList.count < 1 && thamquyenList.count < 1 && tamgiuphuongtienList.count < 1{
            hideBosungKhacphucView(isHidden: true)
        } else {
            populateBosungKhacphucView()
        }
    }

    func populateBosungKhacphucView(){
        if hinhphatbosungList.count > 0 {
            hideHinhphatbosungView(isHidden: false)
            lblHinhphatbosungTitle.numberOfLines = 0
            lblHinhphatbosungTitle.lineBreakMode = NSLineBreakMode.byWordWrapping
            lblHinhphatbosungTitle.text = "Hình phạt bổ sung:"
            lblHinhphatbosungDetails.numberOfLines = 0
            lblHinhphatbosungDetails.lineBreakMode = NSLineBreakMode.byWordWrapping
            lblHinhphatbosungDetails.text = ""
            for bosung in hinhphatbosungList {
                lblHinhphatbosungDetails.text = "\(lblHinhphatbosungDetails.text!)\(bosung.getNoidung())\n"
            }
        } else {
            hideHinhphatbosungView(isHidden: true)
        }

        if bienphapkhacphucList.count > 0 {
            hideBienphapkhacphucView(isHidden: false)
            lblBienphapkhacphucTitle.numberOfLines = 0
            lblBienphapkhacphucTitle.lineBreakMode = NSLineBreakMode.byWordWrapping
            lblBienphapkhacphucTitle.text = "Biện pháp khắc phục:"
            lblBienphapkhacphucDetails.numberOfLines = 0
            lblBienphapkhacphucDetails.lineBreakMode = NSLineBreakMode.byWordWrapping
            lblBienphapkhacphucDetails.text = ""
            for khacphuc in bienphapkhacphucList {
                lblBienphapkhacphucDetails.text = "\(lblBienphapkhacphucDetails.text!)\(khacphuc.getNoidung())\n"
            }
        }else{
            hideBienphapkhacphucView(isHidden: true)
        }

        if tamgiuphuongtienList.count > 0 {
            hideTamgiuPhuongtienView(isHidden: false)
            lblTamgiuPhuongtienTitle.numberOfLines = 0
            lblTamgiuPhuongtienTitle.lineBreakMode = NSLineBreakMode.byWordWrapping
            lblTamgiuPhuongtienTitle.text = "Tạm giữ phương tiện:"
            lblTamgiuPhuongtienDetails.numberOfLines = 0
            lblTamgiuPhuongtienDetails.lineBreakMode = NSLineBreakMode.byWordWrapping
            lblTamgiuPhuongtienDetails.text = "07 ngày"
        }else{
            hideTamgiuPhuongtienView(isHidden: true)
        }

        if thamquyenList.count > 0 {
            hideThamquyenView(isHidden: false)
            lblThamquyenTitle.numberOfLines = 0
            lblThamquyenTitle.lineBreakMode = NSLineBreakMode.byWordWrapping
            lblThamquyenTitle.text = "Biện pháp khắc phục:"
            lblThamquyenDetails.numberOfLines = 0
            lblThamquyenDetails.lineBreakMode = NSLineBreakMode.byWordWrapping
            lblThamquyenDetails.text = ""
            for thamquyen in thamquyenList {
                lblThamquyenDetails.text = "\(lblThamquyenDetails.text!)\(thamquyen.getNoidung())\n"
            }
        }else{
            hideThamquyenView(isHidden: true)
        }
    }

    func populateExtraInfoView(){
        let mpText = getMucphat(id: String(describing: dieukhoan!.getId()))
        let ptText = getPhuongtien(id: String(describing: dieukhoan!.getId()))
        let lvText = getLinhvuc(id: String(describing: dieukhoan!.getId()))
        let dtText = getDoituong(id: String(describing: dieukhoan!.getId()))

        if mpText.characters.count > 0 {
            consLblMucphatHeight.isActive = false
            consLblMucphatDetailsHeight.isActive = false
            lblMucphat.text = mpText
        }else{
            consLblMucphatHeight.isActive = true
            consLblMucphatDetailsHeight.isActive = true
            consLblMucphatHeight.constant =  0
            consLblMucphatDetailsHeight.constant =  0
        }
        if ptText.characters.count > 0 {
            consLblPhuongtienHeight.isActive = false
            consLblPhuongtienDetailsHeight.isActive = false
            lblPhuongtien.text = ptText
        }else{
            consLblPhuongtienHeight.isActive = true
            consLblPhuongtienDetailsHeight.isActive = true
            consLblPhuongtienHeight.constant =  0
            consLblPhuongtienDetailsHeight.constant =  0
        }
        if lvText.characters.count > 0 {
            consLblLinhvucHeight.isActive = false
            consLblLinhvucDetailsHeight.isActive = false
            lblLinhvuc.text = lvText
        }else{
            consLblLinhvucHeight.isActive = true
            consLblLinhvucDetailsHeight.isActive = true
            consLblLinhvucHeight.constant =  0
            consLblLinhvucDetailsHeight.constant =  0
        }
        if dtText.characters.count > 0 {
            consLblDoituongHeight.isActive = false
            consLblDoituongDetailsHeight.isActive = false
            lblDoituong.text = dtText
        }else{
            consLblDoituongHeight.isActive = true
            consLblDoituongDetailsHeight.isActive = true
            consLblDoituongHeight.constant =  0
            consLblDoituongDetailsHeight.constant =  0
        }
    }

    //TODO: Handle scaling images if needed
//    func scaleImage(image: UIImage, targetWidth: CGFloat) -> UIImage {
//        let size = image.size
//
//        let widthRatio  = targetWidth / image.size.width
//
//        //        let ratio:Float = Float(size.width)/Float(size.height)
//
//
//        // Figure out what our orientation is, and use that to form the rectangle
//        var newSize: CGSize
//                newSize = CGSize(width: size.width * widthRatio, height: CGFloat(Float(size.height) * Float(widthRatio)))
//
//        // This is the rect that we've calculated out and this is what is actually used below
//        let rect = CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height)
//
//        // Actually do the resizing to the rect using the ImageContext stuff
//        UIGraphicsBeginImageContextWithOptions(newSize, false, 0.0)
//        image.draw(in: rect)
//        let newImage = UIGraphicsGetImageFromCurrentImageContext()
//        UIGraphicsEndImageContext()
//
//        return newImage!
//    }

    func fillMinhhoaToViewMinhhoa(images: [String]) {
        hideMinhhoaView(isHidden: false)
        viewMinhhoa.translatesAutoresizingMaskIntoConstraints = false

        var order = 0
//        var previousImageView = UIImageView()

        for img in images {
            if (img.replacingOccurrences(of: ".png", with: "").replacingOccurrences(of: "\n", with: "")).trimmingCharacters(in: .whitespacesAndNewlines).characters.count < 1{

            }else{
                let image = UIImage(named: (img.replacingOccurrences(of: ".png", with: "").replacingOccurrences(of: "\n", with: "")).trimmingCharacters(in: .whitespacesAndNewlines))!

                        let imgView = UIImageView(image: scaleImage(image: image, targetWidth: getScreenWidth()))
                imgView.translatesAutoresizingMaskIntoConstraints = false
                imgView.clipsToBounds = true
                imgView.contentMode = UIViewContentMode.scaleAspectFit
                imgView.autoresizesSubviews = true
//                viewMinhhoa.insertSubview(imgView, at: order)
                if order == 0 {
                    if images.count == 1 {
                        generateNewComponentConstraints(parent: viewMinhhoa, topComponent: viewMinhhoa, bottomComponent: viewMinhhoa, component: imgView, top: 0, left: 0, right: 0, bottom: 0, isInside: true)
                    }else{
                        generateNewComponentConstraints(parent: viewMinhhoa, topComponent: viewMinhhoa, component: imgView, top: 0, left: 0, right: 0, isInside: true)
                    }
                }else{
                    if order < (images.count - 1) {
//                        generateNewComponentConstraints(parent: viewMinhhoa, topComponent: previousImageView, component: imgView, top: 0, left: 0, right: 0)
                        generateNewComponentConstraints(parent: viewMinhhoa, topComponent: (viewMinhhoa.subviews.last)!, component: imgView, top: 0, left: 0, right: 0, isInside: false)
                    }else{
//                        generateNewComponentConstraints(parent: viewMinhhoa, topComponent: previousImageView, bottomComponent: viewMinhhoa, component: imgView, top: 0, left: 0, right: 0, bottom: 0)
                        generateNewComponentConstraints(parent: viewMinhhoa, topComponent: (viewMinhhoa.subviews.last)!, bottomComponent: viewMinhhoa, component: imgView, top: 0, left: 0, right: 0, bottom: 0, isInside: false)
                    }
                }
//                previousImageView = imgView
                order += 1
                let tap = UITapGestureRecognizer(target: self, action: #selector(seeMore))
                imgView.isUserInteractionEnabled = true
                imgView.addGestureRecognizer(tap)
            }
        }
    }

    //TODO: Populate thamquyen
//    func getThamquyenList() -> [Dieukhoan] {
//        var thamquyen = [Dieukhoan]()
//
//        return thamquyen
//    }

    func getTamgiuPhuongtienList() -> [Dieukhoan] {
        var tamgiu = [Dieukhoan]()
        let qry = "select distinct dk.id as dkId, dk.so as dkSo, tieude as dkTieude, dk.noidung as dkNoidung, minhhoa as dkMinhhoa, cha as dkCha, vb.loai as lvbID, lvb.ten as lvbTen, vb.so as vbSo, vanbanid as vbId, vb.ten as vbTen, nam as vbNam, ma as vbMa, vb.noidung as vbNoidung, coquanbanhanh as vbCoquanbanhanhId, cq.ten as cqTen, dk.forSearch as dkSearch from tblChitietvanban as dk join tblVanban as vb on dk.vanbanid=vb.id join tblLoaivanban as lvb on vb.loai=lvb.id join tblCoquanbanhanh as cq on vb.coquanbanhanh=cq.id join tblRelatedDieukhoan as rdk on dk.id = rdk.dieukhoanId where (dkCha = \(GeneralSettings.tamgiuPhuongtienParentID) or dkCha in (select id from tblchitietvanban where cha = \(GeneralSettings.tamgiuPhuongtienParentID)) or dkCha in (select id from tblchitietvanban where cha in (select id from tblchitietvanban where cha = \(GeneralSettings.tamgiuPhuongtienParentID)))) and rdk.relatedDieukhoanID = \(dieukhoan!.getId())"
        tamgiu = Queries.searchDieukhoanByQuery(query: qry, vanbanid: specificVanbanId)
        return tamgiu
    }

    func getMucphat(id: String) -> String {
        if DataConnection.database == nil {
            DataConnection.databaseSetup()
        }
        return Queries.searchMucphatInfo(id: id)
    }

    func getPhuongtien(id: String) -> String {
        if DataConnection.database == nil {
            DataConnection.databaseSetup()
        }
        return Queries.searchPhuongtienInfo(id: id)
    }

    func getLinhvuc(id: String) -> String {
        if DataConnection.database == nil {
            DataConnection.databaseSetup()
        }
        return Queries.searchLinhvucInfo(id: id)
    }

    func getDoituong(id: String) -> String {
        if DataConnection.database == nil {
            DataConnection.databaseSetup()
        }
        return Queries.searchDoituongInfo(id: id)
    }

    private ArrayList<Dieukhoan> getChildren(String keyword) {
        return queries.searchChildren(keyword.trim(),vanbanid);
    }

    func getParent(keyword:String) -> [Dieukhoan] {
        if DataConnection.database == nil {
            DataConnection.databaseSetup()
        }
        return Queries.searchDieukhoanByID(keyword: "\(keyword)", vanbanid: specificVanbanId)
    }
}
