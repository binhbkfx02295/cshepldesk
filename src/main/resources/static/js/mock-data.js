// Mock Data for Customer Help Desk Dashboard
const PROGRESS_STATUS_CLASS = {
    1: "pending",
    2: "on-hold",
    3: "resolved",
}

const PROGRESS_STATUS = {
    1: "In progress",
    2: "On hold",
    3: "Resolved",
}

const CATEGORY = {
    1: "Mua Hàng",
    2: "Khiếu Nại",
    3: "Khuyến Mãi",
    4: "Thanh Toán",
    5: "Hoàn Tiền",
}
const EMOTION = {
    1: "Tức giận",
    2: "Tiêu cực",
    3: "Bối rối",
    4: "Trung lập",
    5: "Tích cực",
}

const EMOTION_CLASS = {
    1: "angry",
    2: "negative",
    3: "confused",
    4: "neutral",
    5: "positive",
}

const SATISFACTION = {
    1: "Rất tệ",
    2: "Không hài lòng",
    3: "Trung Lập",
    4: "Khá",
    5: "Rất tốt",
}

const SATISFACTION_CLASS = {
    1: "verybad",
    2: "unpleased",
    3: "neutral",
    4: "pleased",
    5: "verypleased",
}
// Employees
const employees = [
    {
        id: 1,
        name: "Nguyễn Văn A",
        userGroup: "Nhân viên",
        avatar: "https://via.placeholder.com/40",
        status: 1,
        userGroup: "Nhân viên",
        statusTime: "01:22:11",
        ticketCount: 5
    },
    {
        id: 2,
        name: "Trần Thị B",
        userGroup: "Quản trị",
        avatar: "https://via.placeholder.com/40",
        status: 1,
        statusTime: "00:02:58",
        ticketCount: 3
    },
    {
        id: 3,
        name: "Lê Văn C",
        userGroup: "Quản trị",
        avatar: "https://via.placeholder.com/40",
        status: 2,
        statusTime: "00:03:42",
        ticketCount: 0
    },
    {
        id: 4,
        name: "Phạm Thị D",
        userGroup: "Nhân viên",
        avatar: "https://via.placeholder.com/40",
        status: 3,
        statusTime: "01:37:58",
        ticketCount: 7
    },
    {
        id: 5,
        name: "Hoàng Văn E",
        userGroup: "Nhân viên",
        avatar: "https://via.placeholder.com/40",
        status: 2,
        statusTime: "00:31:58",
        ticketCount: 0
    },
    {
        id: 6,
        name: "Ngô Thị F",
        userGroup: "Nhân viên",
        avatar: "https://via.placeholder.com/40",
        status: 3,
        statusTime: "00:17:58",
        ticketCount: 2
    }
];

// Tickets
const tickets = [
    {
        id: "TK-001",
        title: "Vấn đề về thanh toán đơn hàng",
        createdAt: 1714273800000, // 08:30 28/04/2025
        updatedAt: 1714275540000, // 08:59 28/04/2025
        facebookUser: {
            id: 1,
            firstName: "Meo",
            lastName: "Con",
            avtSrc: "img/facebookuser-profile-placeholder.jpg",
        },
        employee: {
            id: 1,
            name: "Nguyễn Văn A",
            userGroup: "Nhân viên",
            avatar: "img/profile-placeholder.jpg",
            status: 1,
            statusTime: "01:22:11",
            ticketCount: 5
        },
        progressStatus: 1,
        category: 1,
        emotion: 3,
        satisfaction: 3
    },
    {
        id: "TK-002",
        title: "Hỏi thăm về khuyến mãi",
        createdAt: 1714278780000, // 09:13 28/04/2025
        updatedAt: 1714280700000, // 09:45 28/04/2025
        facebookUser: {
            id: 2,
            firstName: "Teddie",
            lastName: "Bui",
            avtSrc: "img/facebookuser-profile-placeholder.jpg",
        },
        employee: {
            id: 1,
            name: "Nguyễn Văn A",
            userGroup: "Nhân viên",
            avatar: "img/profile-placeholder.jpg",
            status: 2,
            statusTime: "01:22:11",
            ticketCount: 5
        },
        progressStatus: 2,
        category: 2,
        emotion: 3,
        satisfaction: 3
    },
    {
        id: "TK-003",
        title: "Trả hàng như thế nào",
        createdAt: 1714284600000, // 11:30 28/04/2025
        updatedAt: 1714285140000, // 11:39 28/04/2025
        facebookUser: {
            id: 3,
            firstName: "Yuumi",
            lastName: "Lee",
            avtSrc: "img/facebookuser-profile-placeholder.jpg",
        },
        employee: {
            id: 1,
            name: "Nguyễn Văn A",
            userGroup: "Nhân viên",
            avatar: "img/profile-placeholder.jpg",
            status: 1,
            statusTime: "01:22:11",
            ticketCount: 5
        },
        progressStatus: 3,
        category: 3,
        emotion: 5,
        satisfaction: 5
    }
];


// Facebook Users
const facebookUsers = [
    {
        id: "FB-001",
        firstName: "Minh",
        lastName: "Nguyễn",
        avatarSource: "https://via.placeholder.com/40",
        createdAt: "2023-01-15T10:30:00",
        updatedAt: "2023-06-14T14:20:00"
    },
    {
        id: "FB-002",
        firstName: "Hương",
        lastName: "Trần",
        avatarSource: "https://via.placeholder.com/40",
        createdAt: "2023-02-20T09:15:00",
        updatedAt: "2023-06-15T11:45:00"
    },
    {
        id: "FB-003",
        firstName: "Tuấn",
        lastName: "Lê",
        avatarSource: "https://via.placeholder.com/40",
        createdAt: "2023-03-05T13:40:00",
        updatedAt: "2023-06-10T16:30:00"
    },
    {
        id: "FB-004",
        firstName: "Linh",
        lastName: "Phạm",
        avatarSource: "https://via.placeholder.com/40",
        createdAt: "2023-03-18T08:20:00",
        updatedAt: "2023-06-12T10:15:00"
    },
    {
        id: "FB-005",
        firstName: "Hoa",
        lastName: "Vũ",
        avatarSource: "https://via.placeholder.com/40",
        createdAt: "2023-04-02T15:10:00",
        updatedAt: "2023-06-13T09:45:00"
    },
    {
        id: "FB-006",
        firstName: "Đức",
        lastName: "Hoàng",
        avatarSource: "https://via.placeholder.com/40",
        createdAt: "2023-04-25T11:30:00",
        updatedAt: "2023-06-14T13:20:00"
    },
    {
        id: "FB-007",
        firstName: "Mai",
        lastName: "Ngô",
        avatarSource: "https://via.placeholder.com/40",
        createdAt: "2023-05-10T14:45:00",
        updatedAt: "2023-06-15T10:30:00"
    }
];


const messages = [
    // Messages for TK-001
    { messageId: 4767, ticketId: "TK-001", text: "Xin chào, tôi cần hỗ trợ đơn hàng.", timestamp: 1714273800000, isSenderStaff: false },
    { messageId: 4769, ticketId: "TK-001", text: "Đơn hàng của tôi chưa giao.", timestamp: 1714274040000, isSenderStaff: false },
    { messageId: 4770, ticketId: "TK-001", text: "Xin lỗi bạn, để tôi kiểm tra.", timestamp: 1714274160000, isSenderStaff: true },
    { messageId: 4771, ticketId: "TK-001", text: "Cảm ơn bạn.", timestamp: 1714274280000, isSenderStaff: false },
    { messageId: 4772, ticketId: "TK-001", text: "Đơn hàng của bạn đã ra kho hôm qua.", timestamp: 1714274400000, isSenderStaff: true },
    { messageId: 4774, ticketId: "TK-001", text: "Đây ạ: #123456789", timestamp: 1714274640000, isSenderStaff: true },
    { messageId: 4775, ticketId: "TK-001", text: "Cảm ơn bạn nhiều.", timestamp: 1714274760000, isSenderStaff: false },
    { messageId: 4776, ticketId: "TK-001", text: "Không có gì ạ.", timestamp: 1714274880000, isSenderStaff: true },
    { messageId: 4777, ticketId: "TK-001", text: "Chúc bạn một ngày tốt lành.", timestamp: 1714275000000, isSenderStaff: true },
    { messageId: 4778, ticketId: "TK-001", text: "Bạn cũng vậy nhé.", timestamp: 1714275120000, isSenderStaff: false },
    { messageId: 4779, ticketId: "TK-001", text: "Cần thêm hỗ trợ hãy nhắn tin nhé.", timestamp: 1714275240000, isSenderStaff: true },
    { messageId: 4780, ticketId: "TK-001", text: "Ok bạn.", timestamp: 1714275360000, isSenderStaff: false },
    { messageId: 4781, ticketId: "TK-001", text: "Đã kết thúc hỗ trợ đơn hàng.", timestamp: 1714275480000, isSenderStaff: true },

    // Messages for TK-002
    { messageId: 4782, ticketId: "TK-002", text: "Chào shop, chương trình khuyến mãi còn không?", timestamp: 1714277580000, isSenderStaff: false },
    { messageId: 4783, ticketId: "TK-002", text: "Vâng còn ạ, bạn muốn tìm hiểu sản phẩm nào?", timestamp: 1714277700000, isSenderStaff: true },
    { messageId: 4784, ticketId: "TK-002", text: "Tôi muốn hỏi về sản phẩm giảm cân.", timestamp: 1714277820000, isSenderStaff: false },
    { messageId: 4785, ticketId: "TK-002", text: "Giảm 20% cho đơn từ 500k ạ.", timestamp: 1714277940000, isSenderStaff: true },
    { messageId: 4786, ticketId: "TK-002", text: "Áp dụng cho toàn bộ sản phẩm?", timestamp: 1714278060000, isSenderStaff: false },
    { messageId: 4787, ticketId: "TK-002", text: "Chỉ áp dụng cho nhóm sản phẩm dinh dưỡng thể thao.", timestamp: 1714278180000, isSenderStaff: true },
    { messageId: 4788, ticketId: "TK-002", text: "Có tư vấn dinh dưỡng luôn không?", timestamp: 1714278300000, isSenderStaff: false },
    { messageId: 4789, ticketId: "TK-002", text: "Có ạ, nhân viên tư vấn miễn phí.", timestamp: 1714278420000, isSenderStaff: true },
    { messageId: 4790, ticketId: "TK-002", text: "Tôi đặt đơn luôn nhé.", timestamp: 1714278540000, isSenderStaff: false },
    { messageId: 4791, ticketId: "TK-002", text: "Vâng bạn cung cấp thông tin giao hàng nhé.", timestamp: 1714278660000, isSenderStaff: true },
    { messageId: 4792, ticketId: "TK-002", text: "Tên tôi là Teddie.", timestamp: 1714278780000, isSenderStaff: false },
    { messageId: 4793, ticketId: "TK-002", text: "Ok đã ghi nhận đơn.", timestamp: 1714278900000, isSenderStaff: true },
    { messageId: 4794, ticketId: "TK-002", text: "Tks shop nhiều nhé.", timestamp: 1714279020000, isSenderStaff: false },
    { messageId: 4795, ticketId: "TK-002", text: "Không có gì ạ, cảm ơn bạn.", timestamp: 1714279140000, isSenderStaff: true },
    { messageId: 4796, ticketId: "TK-002", text: "Bye shop.", timestamp: 1714279260000, isSenderStaff: false },

    // Messages for TK-003
    { messageId: 4797, ticketId: "TK-003", text: "Shop ơi, tôi muốn trả hàng.", timestamp: 1714282200000, isSenderStaff: false },
    { messageId: 4798, ticketId: "TK-003", text: "Dạ vâng, lý do trả hàng giúp em nhé.", timestamp: 1714282320000, isSenderStaff: true },
    { messageId: 4799, ticketId: "TK-003", text: "Sản phẩm không như mô tả.", timestamp: 1714282440000, isSenderStaff: false },
    { messageId: 4800, ticketId: "TK-003", text: "Xin lỗi bạn vì trải nghiệm không tốt.", timestamp: 1714282560000, isSenderStaff: true },
    { messageId: 4801, ticketId: "TK-003", text: "Tôi muốn hoàn tiền.", timestamp: 1714282680000, isSenderStaff: false },
    { messageId: 4802, ticketId: "TK-003", text: "Em sẽ hướng dẫn bạn quy trình hoàn tiền ạ.", timestamp: 1714282800000, isSenderStaff: true },
    { messageId: 4803, ticketId: "TK-003", text: "Tôi cần gửi sản phẩm về đâu?", timestamp: 1714282920000, isSenderStaff: false },
    { messageId: 4804, ticketId: "TK-003", text: "Gửi về kho số 1 Quận 1, TP.HCM.", timestamp: 1714283040000, isSenderStaff: true },
    { messageId: 4805, ticketId: "TK-003", text: "Ok, tôi sẽ gửi trong hôm nay.", timestamp: 1714283160000, isSenderStaff: false },
    { messageId: 4806, ticketId: "TK-003", text: "Dạ vâng ạ, cảm ơn bạn.", timestamp: 1714283280000, isSenderStaff: true },
    { messageId: 4807, ticketId: "TK-003", text: "Bao lâu thì nhận hoàn tiền?", timestamp: 1714283400000, isSenderStaff: false },
    { messageId: 4808, ticketId: "TK-003", text: "Trong vòng 3-5 ngày sau khi nhận được hàng.", timestamp: 1714283520000, isSenderStaff: true },
    { messageId: 4809, ticketId: "TK-003", text: "Ok. Cảm ơn shop.", timestamp: 1714283640000, isSenderStaff: false },
    { messageId: 4810, ticketId: "TK-003", text: "Không có gì ạ. Chúc bạn một ngày vui vẻ!", timestamp: 1714283760000, isSenderStaff: true },
    { messageId: 4811, ticketId: "TK-003", text: "Cảm ơn bạn.", timestamp: 1714283880000, isSenderStaff: false },
];
