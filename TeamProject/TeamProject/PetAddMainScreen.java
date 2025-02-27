package TeamProject;

import java.awt.*;
import java.util.Calendar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class PetAddMainScreen extends JFrame {

	private BufferedImage image;
	private JLabel alarmLabel, profileLabel, mainProfileLabel, petProfileLabel, addButtonLabel, logoutLabel, logoLabel;
	RoundedImageLabel imageProfileLabel;
	RoundedImageLabel imageLabel;
	private ImageIcon image2;
	private JButton logoutButton;
	private JLabel welcomeLabel, petNameLabel, petSpeciesLabel, petAgeLabel, petGenderLabel;
	TPMgr mgr = new TPMgr();
	PetBean bean;
	Vector<PetBean> vlist;
	private PetChooseDialog pc;
	private JPanel petaddPanel;
	private JScrollPane scrollPane; // 스크롤 패널
	private byte[] imageBytes, imageBytes1;
	private RoundedImageLabel petImageLabel;

	public PetAddMainScreen() {
		setTitle("프레임 설정");
		setSize(402, 874);
		setUndecorated(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		UserBean bean1 = mgr.showUser(StaticData.user_id);
		vlist = mgr.showPet(StaticData.user_id);

		try {
			image = ImageIO.read(new File("TeamProject/phone_frame.png")); // 투명 PNG 불러오기
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 🔹 공통 마우스 클릭 이벤트 리스너
		MouseAdapter commonMouseListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Object source = e.getSource(); // 클릭된 컴포넌트 확인

				if (source == alarmLabel) {
					System.out.println("🔔 알람 클릭됨!");
					dispose();
					new AlarmMainScreen(PetAddMainScreen.this);
				} else if (source == imageLabel) {
					System.out.println("👤 프로필 클릭됨!");
					dispose();
					new UpdateUserScreen(PetAddMainScreen.this);
				} else if (source == imageProfileLabel) {
					dispose();
					new UpdateUserScreen(PetAddMainScreen.this);
				} else if (source == addButtonLabel) {
					System.out.println("➕ 추가 버튼 클릭됨!");
					if (pc == null) {
						pc = new PetChooseDialog(PetAddMainScreen.this);
						// ZipcodeFrame의 창의 위치를 MemberAWT 옆에 지정
						pc.setLocation(getX() + 25, getY() + 300);
					} else {
						pc.setLocation(getX() + 25, getY() + 300);
						pc.setVisible(true);
					}
					setEnabled(false);
				} else if (source == logoutLabel) {
					dispose();
					mgr.userOut(StaticData.user_id);
					new LoginScreen();
				}
			}
		};

		// 🔹 알람 아이콘
		alarmLabel = createScaledImageLabel("TeamProject/alarm.png", 40, 40);
		if (mgr.nonReadMsg(StaticData.user_id))
			alarmLabel = createScaledImageLabel("TeamProject/alarm_in.png", 40, 40);
		alarmLabel.setBounds(280, 120, 40, 40);
		alarmLabel.addMouseListener(commonMouseListener);
		add(alarmLabel);

		// 로고 아이콘
		logoLabel = createScaledImageLabel("TeamProject/logo2.png", 180, 165);
		logoLabel.setBounds(105, 54, 180, 165);
		add(logoLabel);

		// 메인 프로필 이미지
		byte[] imgBytes = bean1.getUser_image();
		if (imgBytes == null || imgBytes.length == 0) {
			// 기본 프로필 이미지 사용
			ImageIcon icon = new ImageIcon("TeamProject/profile.png");
			Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);

			// RoundedImageLabel 사용
			imageLabel = new RoundedImageLabel(img, 200, 200, 3); // 200은 크기, 3은 둥근 정도
			imageLabel.setBounds(101, 178, 200, 200);
			imageLabel.addMouseListener(commonMouseListener);
			add(imageLabel);
		} else {
			// 사용자 이미지가 있을 경우
			ImageIcon icon = new ImageIcon(imgBytes);
			Image img = icon.getImage();

			// 원본 이미지 크기
			int imgWidth = icon.getIconWidth();
			int imgHeight = icon.getIconHeight();

			// 타겟 크기 (200x200)
			int targetWidth = 200;
			int targetHeight = 200;

			// 비율 유지하면서 자르기 위해 더 많이 필요한 쪽 기준으로 크기 조정
			double targetRatio = (double) targetWidth / targetHeight;
			double imgRatio = (double) imgWidth / imgHeight;

			int cropWidth = imgWidth;
			int cropHeight = imgHeight;

			if (imgRatio > targetRatio) {
				// 원본이 더 넓은 경우 → 가로를 자름
				cropWidth = (int) (imgHeight * targetRatio);
			} else {
				// 원본이 더 높은 경우 → 세로를 자름
				cropHeight = (int) (imgWidth / targetRatio);
			}

			// 중심을 기준으로 자를 영역 계산
			int x = (imgWidth - cropWidth) / 2;
			int y = (imgHeight - cropHeight) / 2;

			// BufferedImage로 자르기
			BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bufferedImage.getGraphics();
			g.drawImage(img, 0, 0, null);
			g.dispose();

			BufferedImage croppedImage = bufferedImage.getSubimage(x, y, cropWidth, cropHeight);

			// 이미지 크기 조정 (200x200)
			Image resizedImg = croppedImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);

			// RoundedImageLabel 사용
			imageLabel = new RoundedImageLabel(resizedImg, 200, 200, 3); // 270은 크기, 3은 둥근 정도
			imageLabel.setBounds(101, 178, 200, 200);
			imageLabel.addMouseListener(commonMouseListener);
			add(imageLabel);
		}

		// 상단 프로필 아이디
		if (imgBytes == null || imgBytes.length == 0) { // 330 120 40 40
			// 기본 프로필 이미지 사용
			ImageIcon icon = new ImageIcon("TeamProject/profile.png");
			Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);

			// RoundedImageLabel 사용
			imageProfileLabel = new RoundedImageLabel(img, 40, 40, 3); // 200은 크기, 3은 둥근 정도
			imageProfileLabel.setBounds(330, 120, 40, 40);
			imageProfileLabel.addMouseListener(commonMouseListener);
			add(imageProfileLabel);
		} else {
			// 사용자 이미지가 있을 경우
			ImageIcon icon1 = new ImageIcon(imgBytes);
			Image img = icon1.getImage();
			
			// 원본 이미지 크기
			int imgWidth = icon1.getIconWidth();
			int imgHeight = icon1.getIconHeight();

			// 타겟 크기 (40x40)
			int targetWidth = 40;
			int targetHeight = 40;

			// 비율 유지하면서 자르기 위해 더 많이 필요한 쪽 기준으로 크기 조정
			double targetRatio = (double) targetWidth / targetHeight;
			double imgRatio = (double) imgWidth / imgHeight;

			int cropWidth = imgWidth;
			int cropHeight = imgHeight;

			if (imgRatio > targetRatio) {
				// 원본이 더 넓은 경우 → 가로를 자름
				cropWidth = (int) (imgHeight * targetRatio);
			} else {
				// 원본이 더 높은 경우 → 세로를 자름
				cropHeight = (int) (imgWidth / targetRatio);
			}

			// 중심을 기준으로 자를 영역 계산
			int x = (imgWidth - cropWidth) / 2;
			int y = (imgHeight - cropHeight) / 2;

			// BufferedImage로 자르기
			BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bufferedImage.getGraphics();
			g.drawImage(img, 0, 0, null);
			g.dispose();

			BufferedImage croppedImage = bufferedImage.getSubimage(x, y, cropWidth, cropHeight);

			// 이미지 크기 조정 (200x200)
			Image resizedImg = croppedImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);

			// RoundedImageLabel 사용
			imageProfileLabel = new RoundedImageLabel(resizedImg, 40, 40, 3); // 40은 크기, 3은 둥근 정도
			imageProfileLabel.setBounds(330, 120, 40, 40);
			imageProfileLabel.addMouseListener(commonMouseListener);
			add(imageProfileLabel);
		}

		// 환영 문구
		welcomeLabel = new JLabel("어서오세요, " + mgr.userName(StaticData.user_id) + "님");
		welcomeLabel.setBounds(135, 401, 134, 20);
		welcomeLabel.setForeground(Color.BLACK);
		add(welcomeLabel);

		// 로그아웃 버튼
		logoutLabel = createScaledImageLabel("TeamProject/logout_icon.png", 40, 40);
		logoutLabel.setBounds(30, 122, 40, 40);
		logoutLabel.addMouseListener(commonMouseListener);
		add(logoutLabel);

		// JPanel 추가
		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (image != null) {
					// 이미지 크기 조정 후 그리기
					Image scaledImage = image.getScaledInstance(402, 874, Image.SCALE_SMOOTH);
					g.drawImage(scaledImage, 0, 0, this);
				}

				// y=158 위치에 가로로 회색 선 그리기
				g.setColor(Color.LIGHT_GRAY); // 선 색을 회색으로 설정
				g.drawLine(22, 165, 379, 165);
				g.drawLine(22, 429, 379, 429);
				g.drawLine(22, 791, 379, 791);
			}
		};

		panel.setOpaque(false);
		panel.setLayout(null);
		add(panel);

		// 🔹 스크롤 가능한 게시글 패널 설정
		petaddPanel = new JPanel();
		petaddPanel.setLayout(new BoxLayout(petaddPanel, BoxLayout.Y_AXIS)); // 세로로 쌓이게 설정
		petaddPanel.setBorder(new LineBorder(Color.WHITE, 1));
		petaddPanel.setBackground(Color.WHITE);

		petAddMain();

		// 🔹 스크롤 패널 추가 (23, 165, 357, 615 영역에 배치)
		scrollPane = new JScrollPane(petaddPanel);
		scrollPane.setBounds(23, 430, 357, 360);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); // 스크롤바 숨기기
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 부드러운 스크롤 유지
		scrollPane.setBorder(new MatteBorder(0, 0, 0, 0, Color.white));
		panel.add(scrollPane);

		// 🔹 추가 버튼 (화면에 고정)
		addButtonLabel = createScaledImageLabel("TeamProject/pet_menu.png", 85, 45);
		addButtonLabel.setBounds(280, 725, 85, 45);
		addButtonLabel.addMouseListener(commonMouseListener);
		addButtonLabel.setOpaque(true);
		addButtonLabel.setBackground(new Color(255, 255, 255, 0));
		addButtonLabel.setVisible(true);
		getLayeredPane().add(addButtonLabel, JLayeredPane.PALETTE_LAYER);

		// 🔹 닫기 버튼
		JButton closeButton = new JButton("X");
		closeButton.setBounds(370, 10, 20, 20);
		closeButton.setBackground(Color.RED);
		closeButton.setForeground(Color.WHITE);
		closeButton.setBorder(BorderFactory.createEmptyBorder());
		closeButton.setFocusPainted(false);
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mgr.userOut(StaticData.user_id);
				System.exit(0);
			}
		});
		panel.add(closeButton);

		setVisible(true);

		if (mgr.nonReadMsg(StaticData.user_id)) {
			new AlarmNewDialog(PetAddMainScreen.this);
			setEnabled(false);
		}
	}

	private void petAddMain() {
		petaddPanel.removeAll();

		for (PetBean pb : vlist) {
			JPanel petAddMainPanel = new JPanel();
			petAddMainPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					dispose();
					new PetHomeScreen(pb.getPet_id());
				}
			});
			petAddMainPanel.setPreferredSize(new Dimension(353, 160)); // 크기 지정
			petAddMainPanel.setMaximumSize(new Dimension(353, 160)); // 최대 크기 고정
			petAddMainPanel.setBackground(Color.WHITE);
//			petAddMainPanel.setBorder(new LineBorder(Color.black, 1));
			petAddMainPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
			petAddMainPanel.setLayout(new BorderLayout(10, 10)); // 여백 포함

//			// 2) 상단 패널 (USER_ID + 날짜)
//			JPanel topPanel = new JPanel(new BorderLayout());

			// 3) 구분선
			JSeparator separator = new JSeparator();
//			separator.setForeground(Color.BLACK);

			// 왼쪽 - 이미지
			byte[] imgBytes = pb.getPet_image();
			if (imgBytes == null || imgBytes.length == 0) {
				ImageIcon icon = new ImageIcon("TeamProject/dog.png");
				Image img = icon.getImage().getScaledInstance(135, 135, image.SCALE_SMOOTH);

				petImageLabel = new RoundedImageLabel(img, 135, 135, 3);
			} else {
				ImageIcon icon = new ImageIcon(imgBytes);
				Image img = icon.getImage();

				// 원본 이미지 크기
				int imgWidth = icon.getIconWidth();
				int imgHeight = icon.getIconHeight();

				// 타겟 크기 (200x200)
				int targetWidth = 200;
				int targetHeight = 200;

				// 비율 유지하면서 자르기 위해 더 많이 필요한 쪽 기준으로 크기 조정
				double targetRatio = (double) targetWidth / targetHeight;
				double imgRatio = (double) imgWidth / imgHeight;

				int cropWidth = imgWidth;
				int cropHeight = imgHeight;

				if (imgRatio > targetRatio) {
					// 원본이 더 넓은 경우 → 가로를 자름
					cropWidth = (int) (imgHeight * targetRatio);
				} else {
					// 원본이 더 높은 경우 → 세로를 자름
					cropHeight = (int) (imgWidth / targetRatio);
				}

				// 중심을 기준으로 자를 영역 계산
				int x = (imgWidth - cropWidth) / 2;
				int y = (imgHeight - cropHeight) / 2;

				// BufferedImage로 자르기
				BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics g = bufferedImage.getGraphics();
				g.drawImage(img, 0, 0, null);
				g.dispose();

				BufferedImage croppedImage = bufferedImage.getSubimage(x, y, cropWidth, cropHeight);

				// 이미지 크기 조정 (200x200)
				Image resizedImg = croppedImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);

				petImageLabel = new RoundedImageLabel(resizedImg, 135, 135, 3);
			}
			// petImageLabel 크기 지정
			petImageLabel.setPreferredSize(new Dimension(135, 135)); // 이미지 크기 지정

			// 4) 본문 패널 (이미지 + 텍스트)
			JPanel contentPanel = new JPanel();
			contentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5)); // 왼쪽 정렬 FlowLayout으로 변경
			contentPanel.setBackground(Color.WHITE);
			contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 위, 왼쪽, 아래, 오른쪽 순서
			contentPanel.add(petImageLabel);

			// 오른쪽 - 이름, 종, 나이, 성별
			JPanel textPanel = new JPanel();
			textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
			textPanel.setBackground(Color.WHITE);

			JLabel nameLabel = new JLabel("이름 : " + pb.getPet_name());
			JLabel speciesLabel = new JLabel("종 : " + pb.getPet_species());
			JLabel ageLabel = new JLabel("나이 : " + pb.getPet_age());
			JLabel genderLabel = new JLabel("성별 : " + pb.getPet_gender());

			textPanel.add(nameLabel);
			textPanel.add(speciesLabel);
			textPanel.add(ageLabel);
			textPanel.add(genderLabel);
			textPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

			contentPanel.add(textPanel, BorderLayout.CENTER);

			// 5) 전체 구성
			petAddMainPanel.add(contentPanel, BorderLayout.CENTER);
//			petAddMainPanel.add(separator, BorderLayout.SOUTH);

			// petaddPanel에 추가
			petaddPanel.add(petAddMainPanel);

			// 각 애완동물 항목 간에 간격을 둔다
			petaddPanel.add(Box.createVerticalStrut(1)); // 0px 간격

			String birth = pb.getPet_age();
			if (!birth.isEmpty()) { // 생일이 기입했을 경우
				// 반려동물 생일 알림
				Calendar calendar = Calendar.getInstance();
				int month1 = calendar.get(Calendar.MONTH) + 1; // 월은 0부터 시작하므로 +1
				int day1 = calendar.get(Calendar.DAY_OF_MONTH);
				String today = String.format("%02d%02d", month1, day1); // 형식: MM월 DD일
				if (mgr.isPetBirth(pb.getPet_id()).equals(birth)) { // 마지막으로 알림 보낸 날짜가 오늘이랑 같을 경우
					// 반응 안함
				} else { // 마지막으로 알림 보낸 날짜가 오늘이 아닌경우
					String[] date = birth.split("\\.");
					String month = date[1];
					String day = date[2];
					if (today.equals(month + day)) { // 오늘이 생일인 경우
						MsgBean mb = new MsgBean();
						mb.setMsg_title(pb.getPet_name() + "의 특별한 날! 생일 축하해요!");
						mb.setMsg_content("안녕하세요! 좋은 소식을 전해 드립니다! \r\n" + "오늘은 바로" + pb.getPet_name() + "의 생일이에요! "
								+ "이 특별한 날을 축하해 주세요! 맛있는 간식과 함께 행복한 시간을 보내길 바래요. \r\n" + pb.getPet_name()
								+ "도 여러분의 사랑을 기다리고 있을 거예요! \r\n" + "즐거운 하루 되세요!");
						mb.setReceiver_id(StaticData.user_id);
						mgr.sendMsg("admin", mb);
						mgr.petBirth(pb.getPet_id(), birth);
					}
				}

				// 건강 검진일 알림

				// 혼합 백신(생후 6주부터 2주간격으로 계속 알림, 15주부터 매년 알림)
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

				// 생일을 LocalDate로 변환
				LocalDate birthday = LocalDate.parse(birth, formatter);
				LocalDate currentDay = LocalDate.now();

				// 6주 후부터 2주 간격으로 5번 알림
				int weeksAfter = 6;
				int interval = 2;
				int reminderCount = 5;

				for (int i = 0; i < reminderCount; i++) {
					LocalDate reminderDate = birthday.plusWeeks(weeksAfter + (i * interval));
					if (reminderDate.equals(currentDay)) { // 검진일이 오늘이라면
						MsgBean mgb = new MsgBean();
						mgb.setMsg_title("반려동물 혼합 백신 검진일 안내");
						mgb.setMsg_content("안녕하세요, " + mgr.showOneUserName(StaticData.user_id) + "님!\r\n"
								+ "소중한 반려동물의 건강을 위해 혼합 백신 검진일이 다가왔음을 알려드립니다.\r\n" + "\r\n" + "검진 대상: ["
								+ pb.getPet_name() + "]\r\n" + "예정일: [" + reminderDate.format(formatter) + "]\r\n"
								+ "\r\n" + "정기적인 백신 접종은 우리 아이의 건강을 지키는 가장 좋은 방법입니다. 잊지 말고 가까운 동물병원을 방문해 주세요! ");
						mgb.setReceiver_id(StaticData.user_id);
						if (mgr.isPetMedic(pb.getPet_id(), "혼합 백신").equals(currentDay.format(formatter))) { // 혼합 백신 알림을
																											// 보낸게 오늘일
																											// 경우
							// 반응 안함
						} else {
							mgr.sendMsg("admin", mgb);
							mgr.petMedic(pb.getPet_id(), currentDay.format(formatter), "혼합 백신");
						}
					}
				}

				// 15주 후부터 1년 간격으로 알림
				LocalDate yearlyStart = birthday.plusWeeks(15);
				if (!currentDay.isBefore(yearlyStart)) {
					for (int i = 0; i <= 50; i++) { // 최대 50년 동안 알림 (조정 가능)
						LocalDate yearlyReminder = yearlyStart.plusYears(i);
						if (yearlyReminder.equals(currentDay)) {
							MsgBean mgb = new MsgBean();
							mgb.setMsg_title("반려동물 혼합 백신 검진일 안내");
							mgb.setMsg_content("안녕하세요, " + mgr.showOneUserName(StaticData.user_id) + "님!\r\n"
									+ "소중한 반려동물의 건강을 위해 혼합 백신 검진일이 다가왔음을 알려드립니다.\r\n" + "\r\n" + "검진 대상: ["
									+ pb.getPet_name() + "]\r\n" + "예정일: [" + yearlyReminder.format(formatter) + "]\r\n"
									+ "\r\n" + "정기적인 백신 접종은 우리 아이의 건강을 지키는 가장 좋은 방법입니다. 잊지 말고 가까운 동물병원을 방문해 주세요! ");
							mgb.setReceiver_id(StaticData.user_id);
							if (mgr.isPetMedic(pb.getPet_id(), "혼합 백신").equals(currentDay.format(formatter))) { // 혼합 백신
																												// 알림을
																												// 보낸게
																												// 오늘일
																												// 경우
								// 반응 안함
							} else {
								mgr.sendMsg("admin", mgb);
								mgr.petMedic(pb.getPet_id(), currentDay.format(formatter), "혼합 백신");
							}
						}
					}
				}

				// 코로나

				// 6주 후부터 2주 간격으로 2번 알림
				weeksAfter = 6;
				interval = 2;
				reminderCount = 2;

				for (int i = 0; i < reminderCount; i++) {
					LocalDate reminderDate = birthday.plusWeeks(weeksAfter + (i * interval));
					if (reminderDate.equals(currentDay)) { // 검진일이 오늘이라면
						MsgBean mgb = new MsgBean();
						mgb.setMsg_title("반려동물 코로나 검진일 안내");
						mgb.setMsg_content("안녕하세요, " + mgr.showOneUserName(StaticData.user_id) + "님!\r\n"
								+ "소중한 반려동물의 건강을 위해 코로나 검진일이 다가왔음을 알려드립니다.\r\n" + "\r\n" + "검진 대상: [" + pb.getPet_name()
								+ "]\r\n" + "예정일: [" + reminderDate.format(formatter) + "]\r\n" + "\r\n"
								+ "정기적인 백신 접종은 우리 아이의 건강을 지키는 가장 좋은 방법입니다. 잊지 말고 가까운 동물병원을 방문해 주세요! ");
						mgb.setReceiver_id(StaticData.user_id);
						if (mgr.isPetMedic(pb.getPet_id(), "코로나").equals(currentDay.format(formatter))) { // 혼합 백신 알림을
																											// 보낸게 오늘일
																											// 경우
							// 반응 안함
						} else {
							mgr.sendMsg("admin", mgb);
							mgr.petMedic(pb.getPet_id(), currentDay.format(formatter), "코로나");
						}
					}
				}

				// 15주 후부터 1년 간격으로 알림
				yearlyStart = birthday.plusWeeks(15);
				if (!currentDay.isBefore(yearlyStart)) {
					for (int i = 0; i <= 50; i++) { // 최대 50년 동안 알림 (조정 가능)
						LocalDate yearlyReminder = yearlyStart.plusYears(i);
						if (yearlyReminder.equals(currentDay)) {
							MsgBean mgb = new MsgBean();
							mgb.setMsg_title("반려동물 코로나 검진일 안내");
							mgb.setMsg_content("안녕하세요, " + mgr.showOneUserName(StaticData.user_id) + "님!\r\n"
									+ "소중한 반려동물의 건강을 위해 코로나 검진일이 다가왔음을 알려드립니다.\r\n" + "\r\n" + "검진 대상: ["
									+ pb.getPet_name() + "]\r\n" + "예정일: [" + yearlyReminder.format(formatter) + "]\r\n"
									+ "\r\n" + "정기적인 백신 접종은 우리 아이의 건강을 지키는 가장 좋은 방법입니다. 잊지 말고 가까운 동물병원을 방문해 주세요! ");
							mgb.setReceiver_id(StaticData.user_id);
							if (mgr.isPetMedic(pb.getPet_id(), "코로나").equals(currentDay.format(formatter))) { // 혼합 백신
																												// 알림을
																												// 보낸게
																												// 오늘일
																												// 경우
								// 반응 안함
							} else {
								mgr.sendMsg("admin", mgb);
								mgr.petMedic(pb.getPet_id(), currentDay.format(formatter), "코로나");
							}
						}
					}
				}

				// 켄넬코프

				// 10주 후부터 2주 간격으로 2번 알림
				weeksAfter = 10;
				interval = 2;
				reminderCount = 2;

				for (int i = 0; i < reminderCount; i++) {
					LocalDate reminderDate = birthday.plusWeeks(weeksAfter + (i * interval));
					if (reminderDate.equals(currentDay)) { // 검진일이 오늘이라면
						MsgBean mgb = new MsgBean();
						mgb.setMsg_title("반려동물 켄넬코프 검진일 안내");
						mgb.setMsg_content("안녕하세요, " + mgr.showOneUserName(StaticData.user_id) + "님!\r\n"
								+ "소중한 반려동물의 건강을 위해 켄넬코프 검진일이 다가왔음을 알려드립니다.\r\n" + "\r\n" + "검진 대상: ["
								+ pb.getPet_name() + "]\r\n" + "예정일: [" + reminderDate.format(formatter) + "]\r\n"
								+ "\r\n" + "정기적인 백신 접종은 우리 아이의 건강을 지키는 가장 좋은 방법입니다. 잊지 말고 가까운 동물병원을 방문해 주세요! ");
						mgb.setReceiver_id(StaticData.user_id);
						if (mgr.isPetMedic(pb.getPet_id(), "켄넬코프").equals(currentDay.format(formatter))) { // 혼합 백신 알림을
																											// 보낸게 오늘일
																											// 경우
							// 반응 안함
						} else {
							mgr.sendMsg("admin", mgb);
							mgr.petMedic(pb.getPet_id(), currentDay.format(formatter), "켄넬코프");
						}
					}
				}

				// 15주 후부터 1년 간격으로 알림
				yearlyStart = birthday.plusWeeks(15);
				if (!currentDay.isBefore(yearlyStart)) {
					for (int i = 0; i <= 50; i++) { // 최대 50년 동안 알림 (조정 가능)
						LocalDate yearlyReminder = yearlyStart.plusYears(i);
						if (yearlyReminder.equals(currentDay)) {
							MsgBean mgb = new MsgBean();
							mgb.setMsg_title("반려동물 켄넬코프 검진일 안내");
							mgb.setMsg_content("안녕하세요, " + mgr.showOneUserName(StaticData.user_id) + "님!\r\n"
									+ "소중한 반려동물의 건강을 위해 켄넬코프 검진일이 다가왔음을 알려드립니다.\r\n" + "\r\n" + "검진 대상: ["
									+ pb.getPet_name() + "]\r\n" + "예정일: [" + yearlyReminder.format(formatter) + "]\r\n"
									+ "\r\n" + "정기적인 백신 접종은 우리 아이의 건강을 지키는 가장 좋은 방법입니다. 잊지 말고 가까운 동물병원을 방문해 주세요! ");
							mgb.setReceiver_id(StaticData.user_id);
							if (mgr.isPetMedic(pb.getPet_id(), "켄넬코프").equals(currentDay.format(formatter))) { // 혼합 백신
																												// 알림을
																												// 보낸게
																												// 오늘일
																												// 경우
								// 반응 안함
							} else {
								mgr.sendMsg("admin", mgb);
								mgr.petMedic(pb.getPet_id(), currentDay.format(formatter), "켄넬코프");
							}
						}
					}
				}

				// 광견병

				// 14주에 1번 알림
				LocalDate reminderDate = birthday.plusWeeks(14);
				if (reminderDate.equals(currentDay)) { // 검진일이 오늘이라면
					MsgBean mgb = new MsgBean();
					mgb.setMsg_title("반려동물 광견병 검진일 안내");
					mgb.setMsg_content("안녕하세요, " + mgr.showOneUserName(StaticData.user_id) + "님!\r\n"
							+ "소중한 반려동물의 건강을 위해 광견병 검진일이 다가왔음을 알려드립니다.\r\n" + "\r\n" + "검진 대상: [" + pb.getPet_name()
							+ "]\r\n" + "예정일: [" + reminderDate.format(formatter) + "]\r\n" + "\r\n"
							+ "정기적인 백신 접종은 우리 아이의 건강을 지키는 가장 좋은 방법입니다. 잊지 말고 가까운 동물병원을 방문해 주세요! ");
					mgb.setReceiver_id(StaticData.user_id);
					if (mgr.isPetMedic(pb.getPet_id(), "광견병").equals(currentDay.format(formatter))) { // 혼합 백신 알림을 보낸게
																										// 오늘일 경우
						// 반응 안함
					} else {
						mgr.sendMsg("admin", mgb);
						mgr.petMedic(pb.getPet_id(), currentDay.format(formatter), "광견병");
					}
				}

				// 15주 후부터 1년 간격으로 알림
				yearlyStart = birthday.plusWeeks(15);
				if (!currentDay.isBefore(yearlyStart)) {
					for (int i = 0; i <= 50; i++) { // 최대 50년 동안 알림 (조정 가능)
						LocalDate yearlyReminder = yearlyStart.plusYears(i);
						if (yearlyReminder.equals(currentDay)) {
							MsgBean mgb = new MsgBean();
							mgb.setMsg_title("반려동물 광견병 검진일 안내");
							mgb.setMsg_content("안녕하세요, " + mgr.showOneUserName(StaticData.user_id) + "님!\r\n"
									+ "소중한 반려동물의 건강을 위해 광견병 검진일이 다가왔음을 알려드립니다.\r\n" + "\r\n" + "검진 대상: ["
									+ pb.getPet_name() + "]\r\n" + "예정일: [" + yearlyReminder.format(formatter) + "]\r\n"
									+ "\r\n" + "정기적인 백신 접종은 우리 아이의 건강을 지키는 가장 좋은 방법입니다. 잊지 말고 가까운 동물병원을 방문해 주세요! ");
							mgb.setReceiver_id(StaticData.user_id);
							if (mgr.isPetMedic(pb.getPet_id(), "광견병").equals(currentDay.format(formatter))) { // 혼합 백신
																												// 알림을
																												// 보낸게
																												// 오늘일
																												// 경우
								// 반응 안함
							} else {
								mgr.sendMsg("admin", mgb);
								mgr.petMedic(pb.getPet_id(), currentDay.format(formatter), "광견병");
							}
						}
					}
				}

				// 신종플루

				// 14주 후부터 2주 간격으로 2번 알림
				weeksAfter = 14;
				interval = 2;
				reminderCount = 2;

				for (int i = 0; i < reminderCount; i++) {
					reminderDate = birthday.plusWeeks(weeksAfter + (i * interval));
					if (reminderDate.equals(currentDay)) { // 검진일이 오늘이라면
						MsgBean mgb = new MsgBean();
						mgb.setMsg_title("반려동물 신종플루 검진일 안내");
						mgb.setMsg_content("안녕하세요, " + mgr.showOneUserName(StaticData.user_id) + "님!\r\n"
								+ "소중한 반려동물의 건강을 위해 신종플루 검진일이 다가왔음을 알려드립니다.\r\n" + "\r\n" + "검진 대상: ["
								+ pb.getPet_name() + "]\r\n" + "예정일: [" + reminderDate.format(formatter) + "]\r\n"
								+ "\r\n" + "정기적인 백신 접종은 우리 아이의 건강을 지키는 가장 좋은 방법입니다. 잊지 말고 가까운 동물병원을 방문해 주세요! ");
						mgb.setReceiver_id(StaticData.user_id);
						if (mgr.isPetMedic(pb.getPet_id(), "신종플루").equals(currentDay.format(formatter))) { // 혼합 백신 알림을
																											// 보낸게 오늘일
																											// 경우
							// 반응 안함
						} else {
							mgr.sendMsg("admin", mgb);
							mgr.petMedic(pb.getPet_id(), currentDay.format(formatter), "신종플루");
						}
					}
				}

				// 구충제

				// 4주 후부터 2달 간격으로 2번 알림
				int startWeeks = 4;
				int monthInterval = 2;

				for (int i = 0; i < 50; i++) {
					LocalDate extraReminderDate = birthday.plusWeeks(startWeeks).plusMonths(i * monthInterval);
					if (extraReminderDate.equals(currentDay)) { // 검진일이 오늘이라면
						MsgBean mgb = new MsgBean();
						mgb.setMsg_title("반려동물 구충제 투약 안내");
						mgb.setMsg_content("안녕하세요, " + mgr.showOneUserName(StaticData.user_id) + "님!\r\n"
								+ "소중한 반려동물의 건강을 위해 구충제 투약이 다가왔음을 알려드립니다.\r\n" + "\r\n" + "투약 대상: [" + pb.getPet_name()
								+ "]\r\n" + "예정일: [" + extraReminderDate.format(formatter) + "]\r\n" + "\r\n"
								+ "정기적인 백신 접종은 우리 아이의 건강을 지키는 가장 좋은 방법입니다. 잊지 말고 가까운 동물병원을 방문해 주세요! ");
						mgb.setReceiver_id(StaticData.user_id);
						if (mgr.isPetMedic(pb.getPet_id(), "구충제").equals(currentDay.format(formatter))) { // 혼합 백신 알림을
																											// 보낸게 오늘일
																											// 경우
							// 반응 안함
						} else {
							mgr.sendMsg("admin", mgb);
							mgr.petMedic(pb.getPet_id(), currentDay.format(formatter), "구충제");
						}
					}
				}

				// 심장사상충

				// 4주 후부터 1달 간격으로 알림
				startWeeks = 4;
				monthInterval = 1;

				for (int i = 0; i < 50; i++) {
					LocalDate extraReminderDate = birthday.plusWeeks(startWeeks).plusMonths(i * monthInterval);
					if (extraReminderDate.equals(currentDay)) { // 검진일이 오늘이라면
						MsgBean mgb = new MsgBean();
						mgb.setMsg_title("반려동물 심장사상충 치료 안내");
						mgb.setMsg_content("안녕하세요, " + mgr.showOneUserName(StaticData.user_id) + "님!\r\n"
								+ "소중한 반려동물의 건강을 위해 심장사상충 치료일이 다가왔음을 알려드립니다.\r\n" + "\r\n" + "치료 대상: ["
								+ pb.getPet_name() + "]\r\n" + "예정일: [" + extraReminderDate.format(formatter) + "]\r\n"
								+ "\r\n" + "정기적인 백신 접종은 우리 아이의 건강을 지키는 가장 좋은 방법입니다. 잊지 말고 가까운 동물병원을 방문해 주세요! ");
						mgb.setReceiver_id(StaticData.user_id);
						if (mgr.isPetMedic(pb.getPet_id(), "심장사상충").equals(currentDay.format(formatter))) { // 혼합 백신 알림을
																											// 보낸게 오늘일
																											// 경우
							// 반응 안함
						} else {
							mgr.sendMsg("admin", mgb);
							mgr.petMedic(pb.getPet_id(), currentDay.format(formatter), "심장사상충");
						}
					}
				}
			}

		}

		petaddPanel.revalidate();
		petaddPanel.repaint();

	}

	private JLabel createScaledImageLabel(String imagePath, int width, int height) {
		ImageIcon icon = new ImageIcon(imagePath);
		Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new JLabel(new ImageIcon(scaledImage));
	}

	public static void main(String[] args) {
		new LoginScreen();
	}
}