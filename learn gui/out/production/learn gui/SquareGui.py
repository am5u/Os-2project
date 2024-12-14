from tkinter import *
from tkinter import messagebox
from threading import Thread

class SquareGui:

    def __init__(self):
        self.BOARD_SIZE = 4
        self.pieceInputs = []
        self.boardPanels = []
        self.solutionStatusLabels = []
        self.make_asquare = Make_asquare()

        # Set up the main frame
        self.frame = Tk()
        self.frame.title("Puzzle Solver")
        self.frame.config(bg="black")
        self.frame.attributes("-zoomed", True)  # Maximize the window

        # Left panel for inputs
        self.leftPanel = Frame(self.frame, bg="black")
        self.leftPanel.pack(side=TOP, fill=BOTH)

        imageLabel = Label(self.leftPanel, bg="black")
        imageLabel.config(image=PhotoImage(file="tt.jpg"))  # replace with your image path
        imageLabel.pack()

        pieceLabels = ["photo/z.png", "photo/I.png", "photo/J.png", "photo/L.png", "photo/O.png", "photo/s.png", "photo/T.png"]
        for pieceLabel in pieceLabels:
            img = PhotoImage(file=pieceLabel)
            label = Label(self.leftPanel, image=img, bg="black")
            label.image = img  # Keep a reference to avoid garbage collection
            label.pack()

            inputField = Entry(self.leftPanel, justify='center', bg="white", fg="black", width=5)
            inputField.insert(0, "0")
            inputField.pack()
            self.pieceInputs.append(inputField)

        # Solve button
        self.solveButton = Button(self.leftPanel, text="Solve", bg="light gray", fg="black", command=self.Solution)
        self.solveButton.pack(side=LEFT)

        # Clear button
        self.clearButton = Button(self.leftPanel, text="Clear", bg="light gray", fg="black", command=self.clearBoard)
        self.clearButton.pack(side=LEFT)

        # Back to Home button
        self.backButton = Button(self.leftPanel, text="Back to Home", bg="light gray", fg="black", command=self.backToHome)
        self.backButton.pack(side=LEFT)

        # Main panel for solution visualization
        self.mainPanel = Frame(self.frame, bg="black")
        self.mainPanel.pack(fill=BOTH, expand=True)

        self.executor_threads = []
        self.frame.mainloop()

    def Solution(self):
        pieceCounts = self.parsePieceCounts()

        if pieceCounts is None:
            return

        pieces = self.definePieces(pieceCounts)

        self.boardPanels.clear()
        self.solutionStatusLabels.clear()
        self.executor_threads.clear()

        for threadIndex in range(Hellowindow.numberOfThreads):
            # GUI: Setup Panel and Labels
            boardPanel = self.createBoardPanel()
            statusLabel = self.createStatusLabel()
            threadPanel = self.createThreadPanel(threadIndex, boardPanel, statusLabel)

            self.mainPanel.update()

            # Logic: Submit Task to Thread
            board = self.initializeBoard()
            thread = WorkerTread(threadIndex, board, pieces, statusLabel, boardPanel, self.make_asquare)
            thread.start()
            self.executor_threads.append(thread)

    def definePieces(self, pieceCounts):
        pieces = []
        pieces.extend([[[1, 0, 0], [1, 1, 1]]] * pieceCounts[3])
        pieces.extend([[[1, 1, 0], [0, 1, 1]]] * pieceCounts[0])
        pieces.extend([[[1, 1, 1, 1]]] * pieceCounts[1])
        pieces.extend([[[0, 0, 1], [1, 1, 1]]] * pieceCounts[2])
        pieces.extend([[[1, 1, 1], [0, 1, 0]]] * pieceCounts[6])
        pieces.extend([[[0, 1, 1], [1, 1, 0]]] * pieceCounts[5])
        pieces.extend([[[1, 1], [1, 1]]] * pieceCounts[4])
        return pieces

    def parsePieceCounts(self):
        pieceCounts = []
        for inputField in self.pieceInputs:
            try:
                count = int(inputField.get().strip())
                if count < 0 or count > 4:
                    messagebox.showerror("Invalid Input", "Error: Input must be between 0 and 4.")
                    return None
                pieceCounts.append(count)
            except ValueError:
                messagebox.showerror("Invalid Input", "Error: Input must be an integer.")
                return None
        return pieceCounts

    def createBoardPanel(self):
        boardPanel = Frame(self.mainPanel, bg="black")
        boardPanel.pack(side=LEFT, fill=BOTH, expand=True)
        self.boardPanels.append(boardPanel)
        return boardPanel

    def createStatusLabel(self):
        statusLabel = Label(self.mainPanel, text="Not Found", bg="black", fg="white")
        statusLabel.pack(side=BOTTOM)
        self.solutionStatusLabels.append(statusLabel)
        return statusLabel

    def createThreadPanel(self, threadIndex, boardPanel, statusLabel):
        threadPanel = Frame(self.mainPanel, bg="black")
        threadNumberLabel = Label(threadPanel, text=f"Thread {threadIndex + 1}", bg="black", fg="white")
        threadNumberLabel.pack(side=TOP)
        boardPanel.pack(in_=threadPanel, side=TOP)
        statusLabel.pack(in_=threadPanel, side=BOTTOM)
        threadPanel.pack(side=LEFT, fill=BOTH, expand=True)
        return threadPanel

    def initializeBoard(self):
        return [[-1] * self.BOARD_SIZE for _ in range(self.BOARD_SIZE)]

    def clearBoard(self):
        for boardPanel in self.boardPanels:
            for widget in boardPanel.winfo_children():
                widget.destroy()
            boardPanel.update()
        for statusLabel in self.solutionStatusLabels:
            statusLabel.config(text="Not Found")
        for pieceInput in self.pieceInputs:
            pieceInput.delete(0, END)
            pieceInput.insert(0, "0")
        for widget in self.mainPanel.winfo_children():
            widget.destroy()
        self.mainPanel.update()

    def backToHome(self):
        self.frame.destroy()
        # Assuming MainFrame is the main screen class
        Hellowindow()