package TeamProject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Vector;

public class PetHomeScreen extends JFrame {
	private BufferedImage image;
	private JLabel backLabel, alarmLabel, profileLabel, petProfileLabel, addButtonLabel, imageLabel;
	RoundedImageLabel imageProfileLabel;
	private JLabel petNameLabel, petSpecLabel, petBirthLabel, petGenderLabel, logoLabel;
//	private JLabel petRcDateLabel, petRcWHLabel, petRecordLabel, petRcVcLabel, petRcCheckLabel, petRcTimeLabel;
	private JLabel photoLabel, homeLabel, commuLabel, voteLabel;
	private JPanel recordPanel;
	private JPanel scrollPanel; // 스크롤 패널
	private JScrollPane scrollPane;
	TPMgr mgr;
	PetBean bean;
	Vector<HRBean> hrV;
	private byte[] imageBytes;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd   HH:mm");

	public PetHomeScreen(int petId) {
		setTitle("프레임 설정");
		setSize(402, 874);
		setUndecorated(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mgr = new TPMgr();
		bean = mgr.showOnePet(petId); // 반려동물 정보 가져오기
		hrV = mgr.showHRPet(petId); // 반려동물의 건강 기록 가져오기
		StaticData.pet_id = petId;
		UserBean bean1 = mgr.showUser(StaticData.user_id);

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
					new AlarmMainScreen(PetHomeScreen.this);
				} else if (source == imageProfileLabel) {
					System.out.println("상단 프로필 클릭됨!");
					dispose();
					new UpdateUserScreen(PetHomeScreen.this);
				} else if (source == imageLabel) {
					System.out.println("👤 프로필 클릭됨!");
					dispose();
					new PetModifyScreen(PetHomeScreen.this);
				} else if (source == addButtonLabel) {
					System.out.println("➕ 추가 버튼 클릭됨!");
					dispose();
					new PetRecordAddScreen(bean, PetHomeScreen.this);
				} else if (source == backLabel) {
					System.out.println("뒤로가기 버튼 클릭됨!");
					dispose();
					new PetAddMainScreen();
				} else if (source == photoLabel) {
					System.out.println("앨범 & 일기 버튼 클릭됨");
					setEnabled(false);
					new AlbumChooseDialog(PetHomeScreen.this);
				} else if (source == homeLabel) {
					System.out.println("홈 버튼 클릭됨");
					dispose();
					new PetHomeScreen(StaticData.pet_id);
				} else if (source == commuLabel) {
					System.out.println("커뮤 버튼 클릭됨");
					dispose();
					new CommuMainScreen();
				} else if (source == voteLabel) {
					System.out.println("투표 버튼 클릭됨");
					dispose();
					new VoteMainScreen();
				}
			}
		};

		// 🔹 상단 뒤로가기 아이콘
		backLabel = createScaledImageLabel("TeamProject/back_button.png", 40, 40);
		backLabel.setBounds(25, 120, 40, 40);
		backLabel.addMouseListener(commonMouseListener);
		add(backLabel);

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

		// 상단 프로필 아이디
		byte[] imgBytes = bean1.getUser_image();
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

		// 펫 프로필 이미지
		byte[] imgBytes1 = bean.getPet_image();
		if (imgBytes1 == null || imgBytes1.length == 0) {
			imageLabel = new JLabel();
			imageLabel = createScaledImageLabel("TeamProject/dog.png", 150, 150);
			imageLabel.setBounds(40, 190, 150, 150);
			imageLabel.addMouseListener(commonMouseListener);
			add(imageLabel);
		} else {
			// 사용자 이미지가 있을 경우
			ImageIcon icon1 = new ImageIcon(imgBytes1);
			Image img = icon1.getImage();

			// 원본 이미지 크기
			int imgWidth = icon1.getIconWidth();
			int imgHeight = icon1.getIconHeight();

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
			RoundedImageLabel roundedProfileImageLabel = new RoundedImageLabel(resizedImg, 150, 150, 3); // 100은 둥근 정도
			roundedProfileImageLabel.setBounds(40, 190, 150, 150);
			roundedProfileImageLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println("👤 프로필 클릭됨!");
					dispose();
					new PetModifyScreen(PetHomeScreen.this);
				}
			});
			add(roundedProfileImageLabel);
		}

		// 🔹 반려동물 이름 라벨
		petNameLabel = new JLabel("이름: " + bean.getPet_name());
		petNameLabel.setBounds(230, 210, 150, 27); // (x, y, 너비, 높이)
		petNameLabel.setForeground(Color.BLACK); // 텍스트 색 설정
		add(petNameLabel);

		// 🔹 반려동물 종 라벨
		petSpecLabel = new JLabel("종: " + bean.getPet_species());
		petSpecLabel.setBounds(230, 240, 150, 27); // (x, y, 너비, 높이)
		petSpecLabel.setForeground(Color.BLACK); // 텍스트 색 설정
		add(petSpecLabel);

		// 🔹 반려동물 생년월일 라벨
		petBirthLabel = new JLabel("생년월일: " + bean.getPet_age());
		petBirthLabel.setBounds(230, 270, 150, 27); // (x, y, 너비, 높이)
		petBirthLabel.setForeground(Color.BLACK); // 텍스트 색 설정
		add(petBirthLabel);

		// 🔹 반려동물 성별 라벨
		petGenderLabel = new JLabel("성별: " + bean.getPet_gender());
		petGenderLabel.setBounds(230, 300, 150, 27); // (x, y, 너비, 높이)
		petGenderLabel.setForeground(Color.BLACK); // 텍스트 색 설정
		add(petGenderLabel);

		// 🔹 추가 버튼 (화면에 고정)
		addButtonLabel = createScaledImageLabel("TeamProject/record_menu.png", 140, 140);
		addButtonLabel.setBounds(280, 690, 100, 100);
		addButtonLabel = createScaledImageLabel("TeamProject/record_menu.png", 85, 45);
		addButtonLabel.setBounds(280, 725, 85, 45);
		addButtonLabel.addMouseListener(commonMouseListener);
		addButtonLabel.setOpaque(true);
		addButtonLabel.setBackground(new Color(255, 255, 255, 0));
		addButtonLabel.setVisible(true);
		getLayeredPane().add(addButtonLabel, JLayeredPane.PALETTE_LAYER);

		// 🔹 앨범 & 일기 버튼
		photoLabel = createScaledImageLabel("TeamProject/photo.png", 60, 60);
		photoLabel.setBounds(37, 785, 60, 60);
		photoLabel.addMouseListener(commonMouseListener);
		add(photoLabel);

		// 🔹 홈 버튼
		homeLabel = createScaledImageLabel("TeamProject/home_click.png", 58, 58);
		homeLabel.setBounds(125, 787, 58, 58);
		homeLabel.addMouseListener(commonMouseListener);
		add(homeLabel);

		// 🔹 커뮤니티 버튼
		commuLabel = createScaledImageLabel("TeamProject/commu.png", 58, 58);
		commuLabel.setBounds(215, 788, 58, 58);
		commuLabel.addMouseListener(commonMouseListener);
		add(commuLabel);

		// 🔹 투표 버튼
		voteLabel = createScaledImageLabel("TeamProject/vote.png", 55, 55);
		voteLabel.setBounds(305, 789, 55, 55);
		voteLabel.addMouseListener(commonMouseListener);
		add(voteLabel);

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
				g.drawLine(22, 369, 379, 369);
				g.drawLine(22, 574, 379, 574);
				g.drawLine(22, 780, 379, 780);
				g.drawLine(111, 780, 111, 851);
				g.drawLine(200, 780, 200, 851);
				g.drawLine(289, 780, 289, 851);

				Graphics2D g2 = (Graphics2D) g; // Graphics를 Graphics2D로 캐스팅
				g2.setColor(Color.black);
				g2.setStroke(new BasicStroke(5)); // 선 두께 5px 설정
				g2.drawLine(135, 841, 262, 841); // (x1, y1) -> (x2, y2)까지 선 그리기
			}
		};

		panel.setOpaque(false);
		panel.setLayout(null);
		add(panel);

		// 🔹 스크롤 가능한 앨범 패널 설정
		recordPanel = new JPanel();
		recordPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // 아이템이 정렬되도록 설정
		recordPanel.setBackground(Color.WHITE);
		recordPanel.setBorder(new LineBorder(Color.WHITE, 1));

		// 🔹 스크롤 패널 추가 (23, 165, 357, 615 영역에 배치)
		scrollPane = new JScrollPane(recordPanel);
		scrollPane.setBounds(23, 370, 357, 410);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); // 스크롤바 숨기기
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 부드러운 스크롤 유지
		scrollPane.setBorder(new MatteBorder(0, 0, 0, 0, Color.white));
		panel.add(scrollPane);

		addRecord();

		// 닫기 버튼
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
	}

	/**
	 * 레코드 게시글 추가 메서드
	 */
	// 레코드 게시글 추가
	private void addRecord() {
		recordPanel.setLayout(new BoxLayout(recordPanel, BoxLayout.Y_AXIS)); // 수직 정렬

		for (HRBean hr : hrV) {
			// 1) 전체 항목을 감싸는 패널
			JPanel recordItemPanel = new JPanel();
			recordItemPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					dispose();
					new PetRecordModifyScreen(hr, PetHomeScreen.this, hr.getRecord_id());
				}
			});

			recordItemPanel.setPreferredSize(new Dimension(353, 171));
			recordItemPanel.setMaximumSize(new Dimension(353, 171));
			recordItemPanel.setBackground(Color.WHITE);
//			recordItemPanel.setBorder(new LineBorder(Color.black, 1));
			recordItemPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
			recordItemPanel.setLayout(new BorderLayout(10, 10));

			// 2) 전체 텍스트를 하나의 패널로 묶기
			JPanel textPanel = new JPanel();
			textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
			textPanel.setBackground(Color.WHITE);

			JLabel dateText = new JLabel("날짜: " + sdf.format(hr.getHr_date()));
			JLabel WHText = new JLabel("키: " + hr.getHeight() + "cm " + "몸무게: " + hr.getWeight() + "kg");
			JLabel recordText = new JLabel("진료 기록: " + hr.getMedical_history());
			JLabel vaStatusText = new JLabel("예방접종 상태: " + hr.getVaccination_status());
			JLabel chkStatusText = new JLabel("체크해야 할 정보: " + hr.getCheckup_status());
			JLabel recordDateText = new JLabel("진료 관련 시간: " + hr.getDate());

			// Text들 추가
			textPanel.add(dateText);
			textPanel.add(Box.createVerticalStrut(10));
			textPanel.add(WHText);
			textPanel.add(Box.createVerticalStrut(10));
			textPanel.add(recordText);
			textPanel.add(Box.createVerticalStrut(10));
			textPanel.add(vaStatusText);
			textPanel.add(Box.createVerticalStrut(10));
			textPanel.add(chkStatusText);
			textPanel.add(Box.createVerticalStrut(10));
			textPanel.add(recordDateText);

			recordItemPanel.add(textPanel, BorderLayout.CENTER);
			recordPanel.add(recordItemPanel);
			recordPanel.add(Box.createVerticalStrut(5)); // 아이템 간 간격
		}

		// 크기 갱신
		int rows = (recordPanel.getComponentCount() + 1);
		recordPanel.setMaximumSize(new Dimension(353, rows * 171));

		recordPanel.revalidate();
		recordPanel.repaint();

		scrollPane.revalidate();
	}

	/**
	 * 이미지 크기를 조정하여 JLabel을 생성하는 메서드
	 */
	private JLabel createScaledImageLabel(String imagePath, int width, int height) {
		ImageIcon icon = new ImageIcon(imagePath);
		Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new JLabel(new ImageIcon(scaledImage));
	}

	public static void main(String[] args) {
		new LoginScreen();
	}
}
